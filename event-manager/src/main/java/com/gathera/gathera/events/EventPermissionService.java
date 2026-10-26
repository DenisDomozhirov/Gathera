package com.gathera.gathera.events;


import com.gathera.gathera.Users.User;
import com.gathera.gathera.Users.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class EventPermissionService {

    public void checkCanModify(EventsEntity events, User currentUser){
        boolean isOwner = events.getUsers().getId().equals(currentUser.id());
        boolean isAdmin = currentUser.role() == UserRole.ADMIN;

        if(!isOwner && !isAdmin){
            throw new AccessDeniedException(
                    "Only owner or ADMIN can modifying this!"
            );
        }
    }

}
