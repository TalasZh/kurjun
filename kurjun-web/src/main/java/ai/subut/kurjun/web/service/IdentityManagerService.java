package ai.subut.kurjun.web.service;


import java.util.List;

import ai.subut.kurjun.model.identity.User;
import ai.subut.kurjun.model.identity.UserSession;


/**
 *
 */
public interface IdentityManagerService extends BaseService
{
    //*************************************
    List<User> getAllUsers();


    //*************************************
    User getUser( String userId );


    //*************************************
    User addUser( String publicKeyASCII );


    //*************************************
    User authenticateUser( String fingerprint, String authzMessage );


    //*************************************
    UserSession loginUser( String fingerprint, String authzMessage );


    //*************************************
    UserSession loginPublicUser();

    //*************************************
    User setSystemOwner( String publicKeyASCII );

    //*************************************
    User getSystemOwner();
}
