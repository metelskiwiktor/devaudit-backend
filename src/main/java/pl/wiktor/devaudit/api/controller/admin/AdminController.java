package pl.wiktor.devaudit.api.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public void syncUsers() {
        LOGGER.info("Syncing users...");
        int synced = userSyncService.syncUsers();
        LOGGER.info("Synced {} users", synced);
    }
}
