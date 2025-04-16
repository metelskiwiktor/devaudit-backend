package pl.wiktor.devaudit.api.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wiktor.devaudit.api.controller.response.SyncUsersResponse;
import pl.wiktor.devaudit.domain.admin.UserSyncService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final UserSyncService userSyncService;

    public AdminController(UserSyncService userSyncService) {
        this.userSyncService = userSyncService;
    }

    @GetMapping("/sync-users")
    public ResponseEntity<SyncUsersResponse> syncUsers() {
        LOGGER.info("Received request to sync users");
        int syncedCount = userSyncService.syncUsers();
        LOGGER.info("Sync completed, returned {} synced users", syncedCount);
        return ResponseEntity.ok(new SyncUsersResponse(syncedCount));
    }
}
