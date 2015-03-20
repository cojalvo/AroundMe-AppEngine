package com.aroundme.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.aroundme.MessageEndpoint;
import com.aroundme.DAL.Message;
import com.aroundme.DAL.OfyService;
import com.aroundme.DAL.User;
import com.aroundme.DAL.UserLocation;
import com.aroundme.api.common.AroundMeServerUtils;
import com.aroundme.api.common.UserAroundMe;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.base.Strings;
import com.googlecode.objectify.Key;

@Api(name = "aroundmeapi", version = "v1")
public class AroundMeEndpoint {
	@ApiMethod(name = "register", path = "register")
	public User register(User user) throws Exception {
		if (user == null)
			throw new UnauthorizedException("User can't be null");
		if (Strings.isNullOrEmpty(user.getMail())
				|| Strings.isNullOrEmpty(user.getPassword()))
			throw new UnauthorizedException("Mail or password can't be empety");
		if (Strings.isNullOrEmpty(user.getRegistrationId()))
			throw new UnauthorizedException("Registration ID can't be empety");
		User u = getUser(user.getMail());
		if (u != null)
			throw new EntityExistsException("The mail is already in used.");
		OfyService.ofy().save().entity(user).now();
		return user;

	}

	@ApiMethod(name = "login", path = "login")
	public User login(@Named("mail") String mail,
			@Named("password") String password,
			@Named("registrationId") String registrationId) {
		User u = getUser(mail);
		if (u == null)
			return null;
		if (u.getPassword().equals(password)) {
			u.setRegistrationId(registrationId);
			OfyService.ofy().save().entity(u).now();
			return u;
		}
		return null;
	}

	@ApiMethod(name = "sendMessage", path = "sendMessage")
	public void sendMessage(Message message) throws Exception {
		if (message == null)
			throw new UnauthorizedException("Message is null");
		if (Strings.isNullOrEmpty(message.getFrom())
				|| Strings.isNullOrEmpty(message.getTo()))
			throw new UnauthorizedException("Missing mandatory fields.");
		User toUser = getUser(message.getTo());
		User fromUser = getUser(message.getFrom());
		if (toUser == null)
			throw new Exception("The reciever is not exists in the system.");
		if (fromUser == null)
			throw new Exception("The sender is not exists in the system.");
		OfyService.ofy().save().entity(message).now();
		String m = "" + message.getId();
		message.setDownloaded(false);
		MessageEndpoint.doSendViaGcm(m, "newMessage", toUser);
	}

	@ApiMethod(name = "getMessage", path = "getMessage")
	public Message getMessage(@Named("messageId") long messageId) {
		Message m = OfyService.ofy().load()
				.key(Key.create(Message.class, messageId)).now();
		if (m == null)
			throw new EntityNotFoundException(
					"message with the specified ID is not exists.");
		m.setDownloaded(true);
		OfyService.ofy().save().entity(m).now();
		return m;

	}

	@ApiMethod(name = "sendGcmMessage", path = "sendGcmMessage")
	public void sendGcmMessage(@Named("mail") String mail,
			@Named("message") String message) throws IOException {
		User u = getUser(mail);

		if (u == null)
			throw new EntityNotFoundException("The mail is not exists.");
		MessageEndpoint.doSendViaGcm(message, "message", u);
	}

	@ApiMethod(name = "reportUserLocation", path = "reportUserLocation")
	public void reportUserLocation(@Named("mail") String mail, GeoPt point)
			throws Exception {
		User u = getUser(mail);
		if (u == null)
			throw new EntityNotFoundException(
					"Canot find user with the specified mail");
		if (point == null)
			throw new UnauthorizedException("Location is empety");

		UserLocation location = u.getLocation();
		if (location == null)
			location = new UserLocation();
		location.setPoint(point);
		location.setTimeStamp(new Date());
		OfyService.ofy().save().entity(location).now();
		u.setLocation(location);
		OfyService.ofy().save().entity(u).now();

	}

	@ApiMethod(name = "getUsersAroundMe", path = "getUsersAroundMe")
	public Collection<UserAroundMe> getUsersAroundMe(@Named("lat") float lat,
			@Named("lng") float lng, @Named("userMail") String userMail,
			@Named("radius") int radius) {

		List<UserAroundMe> res = new ArrayList<UserAroundMe>();
		List<User> all = OfyService.ofy().load().type(User.class)
				.filter("mail !=", userMail).list();
		if (all != null) {
			for (User user : all) {
				UserLocation loc = user.getLocation();
				if (loc == null)
					continue;
				GeoPt pt = new GeoPt(lat, lng);
				double distance = AroundMeServerUtils.distance(pt, user
						.getLocation().getPoint(), 'K') * 1000;
				if (distance <= radius) {
					UserAroundMe u = new UserAroundMe();
					u.setLastSeen(loc.getTimeStamp());
					u.setLocation(loc.getPoint());
					u.setDisplayName(user.getFullName());
					u.setMail(user.getMail());
					res.add(u);
				}
			}
		}
		return res;
	}

	@ApiMethod(name = "getAllUsers", path = "getAllUsers")
	public Collection<UserAroundMe> getAllUsers(
			@Named("userMail") String userMail) {

		List<UserAroundMe> res = new ArrayList<UserAroundMe>();
		List<User> all = OfyService.ofy().load().type(User.class)
				.filter("mail !=", userMail).list();
		if (all != null) {
			for (User user : all) {
				UserLocation loc = user.getLocation();
				UserAroundMe u = new UserAroundMe();
				u.setLastSeen(loc.getTimeStamp());
				u.setLocation(loc.getPoint());
				u.setDisplayName(user.getFullName());
				u.setMail(user.getMail());
				res.add(u);
			}
		}

		return res;
	}

	private User getUser(String mail) {
		User u = null;
		List<User> res = OfyService.ofy().load().type(User.class)
				.filter("mail", mail).list();
		if (res != null && res.size() == 1)
			u = res.get(0);
		return u;
	}

}
