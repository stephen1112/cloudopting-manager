package eu.cloudopting.service;

import java.util.*;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cloudopting.domain.Authority;
import eu.cloudopting.domain.Organizations;
import eu.cloudopting.domain.User;
import eu.cloudopting.events.api.preconditions.ServicePreconditions;
import eu.cloudopting.repository.AuthorityRepository;
import eu.cloudopting.repository.OrganizationRepository;
import eu.cloudopting.repository.PersistentTokenRepository;
import eu.cloudopting.repository.UserRepository;
import eu.cloudopting.security.SecurityUtils;
import eu.cloudopting.service.util.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;
    
    @Inject
    private OrganizationRepository organizationRepository;
    
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(key);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
        return Optional.empty();
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date oneDayAgo = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));

        return userRepository.findOneByResetKey(key)
                .filter(user -> user.getActivated() && user.getResetDate().after(oneDayAgo))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    userRepository.save(user);
                    return user;
                });

    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
                .filter(user -> user.getActivated())
                .map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(new Date());
                    userRepository.save(user);
                    return user;
                });
        //return Optional.empty();
    }

    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey, Long organizationId) {
        return createUserInformation(login, password, firstName, lastName, email, langKey, organizationId, null);
    }

    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey, Long organizationId, List<String> roles) {
        User newUser = new User();
        Set<Authority> authorities = new HashSet<>();
        if(roles==null || roles.isEmpty()) {
            authorities.add(authorityRepository.findOne("ROLE_SUBSCRIBER"));
        } else {
            for(String role : roles) {
                authorities.add(authorityRepository.findOne(role));
            }
        }
        authorities.add(authorityRepository.findOne("ROLE_USER"));

        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        newUser.setAuthorities(authorities);
        setUserOrganization(newUser, organizationId);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    
    public User setUserActivatedFlag(long userId, boolean activated){
    	User user = userRepository.findOne(userId);
    	user.setActivated(activated);
    	user.setActivationKey(RandomUtil.generateActivationKey());
    	return userRepository.save(user);
    }
    
    public void updateUserInformation(String firstName, String lastName, String email) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void updateUserInformation(long userId, String firstName, String lastName, String email, Long organizationId) {
        updateUserInformation(userId, firstName, lastName, email, organizationId, null);
    }
    public void updateUserInformation(long userId, String firstName, String lastName, String email, Long organizationId, List<String> roles) {
    	User user = userRepository.findOne(userId);
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	user.setEmail(email);
    	setUserOrganization(user, organizationId);

        if(roles!=null && !roles.isEmpty()) {
            Set<Authority> authorities = new HashSet<>();
            for(String role : roles) {
                authorities.add(authorityRepository.findOne(role));
            }
            user.setAuthorities(authorities);
        }
    	userRepository.save(user);
    }
    
    private void setUserOrganization(User user, Long organizationId){
    	if(organizationId != null){
    		Organizations organization = organizationRepository.findOne(organizationId);
    		if(organization != null){
    			user.setOrganizationId(organization);
    			organization.getTUsers().add(user);
    		}
    	}
    }
    
    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u-> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        } );
    }

//    @Transactional(readOnly = true)
//    public User getUserWithAuthorities() {
//        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
//        currentUser.getAuthorities().size(); // eagerly load the association
//        return currentUser;
//    }

    @Transactional(readOnly = true)
    public User getUserAndInitLazyCollections() {
        User currentUser = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
        currentUser.getAuthorities().size(); // eagerly load the association
        if(currentUser.getOrganizationId() != null){
        	currentUser.getOrganizationId().getCloudAccountss().size(); // eagerly load the association
        }
        return currentUser;
    }
    
    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).stream().forEach(token ->{
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    public User loadUserByLogin(String userName){
        User user= userRepository.findOneByLogin(userName).get();
        user.getOrganizationId();
        return user;
    }
    
    public List<User> findAllByCurrentUserOrg(){
    	List<User> users = userRepository.findAllByCurrentUserOrg();
		for(User user : users){
			user.getAuthorities().size();
		}
		return users;
    }
    
    @PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.organizationId.getId() == principal.organizationId")
    public User findOneByCurrentUserOrg(final Long idUser){
    	User user = userRepository.findOne(idUser);
    	ServicePreconditions.checkEntityExists(user);
    	user.getAuthorities().size(); //init roles collection
		return user;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(long id){
    	User user = userRepository.findOne(id);
    	ServicePreconditions.checkEntityExists(user);
    	userRepository.delete(id);
    }
}
