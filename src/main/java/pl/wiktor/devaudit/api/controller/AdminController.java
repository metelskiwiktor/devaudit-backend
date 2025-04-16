package pl.wiktor.devaudit.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wiktor.devaudit.api.response.SyncUsersResponse;
import pl.wiktor.devaudit.domain.admin.UserSyncService;

/**
 * AdminController is responsible for handling administrative tasks related to user synchronization.
 * Can be only accessed by mentors with admin privileges.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final UserSyncService userSyncService;

    public AdminController(UserSyncService userSyncService) {
        this.userSyncService = userSyncService;
    }

    /**
     * Endpoint to synchronize users with the external system. Updates only basic user information (email, role).
     * This endpoint is intended for administrative use only.
     *
     * @return ResponseEntity containing the number of synced users.
     */
    @GetMapping("/sync-users")
    public ResponseEntity<SyncUsersResponse> syncUsers() {
        LOGGER.info("Received request to sync users");
        int syncedCount = userSyncService.syncUsers();
        LOGGER.info("Sync completed, returned {} synced users", syncedCount);
        return ResponseEntity.ok(new SyncUsersResponse(syncedCount));
    }

}
