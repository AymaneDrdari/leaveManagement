//package net.pfe.EmailSender;
//
//import net.pfe.dto.collab.CollaborateurDTO;
//import net.pfe.service.interf.CollaborateurService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Component
//public class CongeScheduler {
//
//    private static final Logger logger = LoggerFactory.getLogger(CongeScheduler.class);
//
//    @Autowired
//    private final CollaborateurService collaborateurService;
//    @Autowired
//    private final EmailService emailService;
//
//    public CongeScheduler(CollaborateurService collaborateurService, EmailService emailService) {
//        this.collaborateurService = collaborateurService;
//        this.emailService = emailService;
//    }
//   // @Scheduled(cron = "0 0 9 * * MON-FRI") // Executes every day at 9 AM from Monday to Friday
//
//    @Scheduled(fixedRate = 30000) // Exécute toutes les 30 secondes
//    public void sendDailyCongeReport() {
//        logger.info("Méthode sendDailyCongeReport exécutée");
//        LocalDate today = LocalDate.now();
//        List<CollaborateurDTO> collaborateursEnConge = collaborateurService.getCollaborateursEnConge(today);
//        logger.info("Nombre de collaborateurs en congé : {}", collaborateursEnConge.size());
//
//        if (collaborateursEnConge.isEmpty()) {
//            logger.info("Aucun collaborateur en congé aujourd'hui.");
//            return; // Arrête l'exécution si la liste est vide
//        }
//
//        Map<String, List<CollaborateurDTO>> congeParEquipe = collaborateursEnConge.stream()
//                .collect(Collectors.groupingBy(collaborateur -> collaborateur.getEquipe().getNom()));
//
//        congeParEquipe.forEach((equipe, collaborateurs) -> {
//            String emailContent = generateEmailContent(collaborateurs, equipe);
//            List<String> chefEmails = collaborateurService.getChefsEquipeEmails(equipe);
//            logger.info("Envoi d'email à : {}", chefEmails);
//            emailService.sendEmail(chefEmails, "Rapport de congé du jour", emailContent);
//        });
//    }
//
//    private String generateEmailContent(List<CollaborateurDTO> collaborateurs, String equipe) {
//        StringBuilder content = new StringBuilder();
//        content.append(String.format("%d personne(s) absente(s) aujourd'hui dans l'équipe %s:\n", collaborateurs.size(), equipe));
//
//        for (CollaborateurDTO collaborateur : collaborateurs) {
//            content.append(String.format("%s %s est absent(e) jusqu'à %s\n",
//                    collaborateur.getPrenom(),
//                    collaborateur.getNom(),
//                    collaborateur.getDateSortieProjet())); // Assuming this is the leave end date
//        }
//        return content.toString();
//    }
//}
