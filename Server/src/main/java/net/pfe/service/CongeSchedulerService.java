package net.pfe.service;
import net.pfe.service.interf.CollaborateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CongeSchedulerService {

    @Autowired
    private CollaborateurService collaborateurService;

    @Scheduled(cron = "0 0 9 * * MON-FRI") // Tous les jours à 9h00 du lundi au vendredi

    //@Scheduled(cron = "0 * * * * *") // Toutes les minutes pour les tests

    public void sendDailyReports() {
        LocalDate today = LocalDate.now();
        collaborateurService.sendDailyReportToTeamLeaders(today);
    }

}
