package com.example.gymmanagement.services;

import com.example.gymmanagement.dto.ChangeMembershipDTO;
import com.example.gymmanagement.dto.RegisterDTO;
import com.example.gymmanagement.entities.MembershipStatus;
import com.example.gymmanagement.entities.User;
import com.example.gymmanagement.entities.UserRole;
import com.example.gymmanagement.repositories.AttendanceRepository;
import com.example.gymmanagement.repositories.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AttendanceRepository attendanceRepository;

    public void setInitialMembershipFromDTO(User user, RegisterDTO dto) {
        String membershipType = dto.getMembershipType();
        String duration = dto.getMembershipDuration();

        // Μετατροπή String -> LocalDate (Για να λυθεί το θέμα του Jackson)
        LocalDate customStart = (dto.getCustomStartDate() != null && !dto.getCustomStartDate().isEmpty())
                ? LocalDate.parse(dto.getCustomStartDate()) : null;
        LocalDate customEnd = (dto.getCustomEndDate() != null && !dto.getCustomEndDate().isEmpty())
                ? LocalDate.parse(dto.getCustomEndDate()) : null;

        if (membershipType == null || membershipType.isBlank()) {
            throw new BadRequestException("Ο τύπος συνδρομής είναι υποχρεωτικός.");
        }
        user.setMembershipType(membershipType);

        if ("CUSTOM".equalsIgnoreCase(duration) && customStart != null && customEnd != null) {
            if (customStart.isAfter(customEnd)) {
                throw new BadRequestException("Η ημερομηνία έναρξης δεν μπορεί να είναι μετά την ημερομηνία λήξης.");
            }
            user.setMembershipStartDate(customStart);
            user.setMembershipEndDate(customEnd);
        } else if (duration != null && !duration.isBlank()) {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate;
            switch (duration.toUpperCase()) {
                case "3-MONTH":
                    endDate = startDate.plusMonths(3);
                    break;
                case "6-MONTH":
                    endDate = startDate.plusMonths(6);
                    break;
                case "12-MONTH":
                    endDate = startDate.plusYears(1);
                    break;
                default:
                    throw new BadRequestException("Μη έγκυρη διάρκεια συνδρομής.");
            }
            user.setMembershipStartDate(startDate);
            user.setMembershipEndDate(endDate);
        } else if ("PERSONAL".equalsIgnoreCase(membershipType)) {
            user.setMembershipStartDate(LocalDate.now());
            user.setMembershipEndDate(LocalDate.now().plusMonths(1));
        } else {
            throw new BadRequestException("Η διάρκεια της συνδρομής είναι υποχρεωτική.");
        }

        user.setMembershipStatus(MembershipStatus.ACTIVE);
    }

    public void changeMembership(User user, ChangeMembershipDTO dto) {
        String newMembershipType = dto.getMembershipType();
        String duration = dto.getMembershipDuration();

        // Προσοχή: Εδώ υποθέτουμε ότι το ChangeMembershipDTO έχει ακόμα LocalDate.
        // Αν το αλλάξεις σε String, πρέπει να βάλεις LocalDate.parse() όπως πάνω.
        LocalDate customStart = dto.getCustomStartDate();
        LocalDate customEnd = dto.getCustomEndDate();

        if (newMembershipType == null || newMembershipType.isBlank()) {
            throw new BadRequestException("Ο τύπος συνδρομής είναι υποχρεωτικός.");
        }

        user.setMembershipType(newMembershipType);

        if ("CUSTOM".equalsIgnoreCase(duration) && customStart != null && customEnd != null) {
            if (customStart.isAfter(customEnd)) {
                throw new BadRequestException("Η ημερομηνία έναρξης δεν μπορεί να είναι μετά την ημερομηνία λήξης.");
            }
            user.setMembershipStartDate(customStart);
            user.setMembershipEndDate(customEnd);
        } else if (duration != null && !duration.isBlank()) {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate;
            switch (duration.toUpperCase()) {
                case "3-MONTH":
                    endDate = startDate.plusMonths(3);
                    break;
                case "6-MONTH":
                    endDate = startDate.plusMonths(6);
                    break;
                case "12-MONTH":
                    endDate = startDate.plusYears(1);
                    break;
                default:
                    throw new BadRequestException("Μη έγκυρη διάρκεια συνδρομής.");
            }
            user.setMembershipStartDate(startDate);
            user.setMembershipEndDate(endDate);
        } else if ("PERSONAL".equalsIgnoreCase(newMembershipType)) {
            user.setMembershipStartDate(LocalDate.now());
            user.setMembershipEndDate(LocalDate.now().plusMonths(1));
        } else {
            throw new BadRequestException("Η διάρκεια της συνδρομής είναι υποχρεωτική.");
        }

        user.setMembershipStatus(MembershipStatus.ACTIVE);

        // Χρήση της custom μεθόδου updateUser του Repository (Manual Transaction)
        userRepository.updateUser(user);
    }

    public User pauseMembership(Long userId, int daysToPause, boolean isAdminAction) {
        User user = getUserById(userId);
        if (user == null || !user.isMember()) {
            throw new NotFoundException("Δεν βρέθηκε μέλος με αυτό το ID.");
        }
        if (user.getMembershipStatus() == MembershipStatus.PAUSED) {
            throw new BadRequestException("Η συνδρομή είναι ήδη σε παύση.");
        }
        if (user.getMembershipEndDate() == null) {
            throw new BadRequestException("Δεν υπάρχει ενεργή συνδρομή για παύση.");
        }

        user.setMembershipStatus(MembershipStatus.PAUSED);
        user.setPauseEndDate(LocalDate.now().plusDays(daysToPause));
        user.setMembershipEndDate(user.getMembershipEndDate().plusDays(daysToPause));
        user.setPausedByAdmin(isAdminAction);

        return userRepository.updateUser(user);
    }

    public User resumeMembership(Long userId, boolean isAdminAction) {
        User user = getUserById(userId);
        if (user == null || !user.isMember()) {
            throw new NotFoundException("Δεν βρέθηκε μέλος με αυτό το ID.");
        }
        if (user.getMembershipStatus() != MembershipStatus.PAUSED) {
            throw new BadRequestException("Η συνδρομή δεν είναι σε παύση.");
        }
        if (user.getPausedByAdmin() && !isAdminAction) {
            throw new ForbiddenException("Επικοινωνήστε με το Διαχειριστή. Μόνο ένας διαχειριστής μπορεί να άρει αυτή την παύση.");
        }

        user.setMembershipStatus(MembershipStatus.ACTIVE);
        user.setPauseEndDate(null);
        user.setPausedByAdmin(false);

        return userRepository.updateUser(user);
    }

    public void deleteUser(Long id) {
        attendanceRepository.deleteByMemberId(id);
        userRepository.deleteById(id);
    }

    public List<User> getAllTrainers(boolean includeInactive) {
        return userRepository.findAllTrainers(includeInactive);
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<User> getAllMembers() { return userRepository.findAllMembers(); }
    public User getUserById(Long id) { return userRepository.findById(id); }
    public User createUser(User user) { return userRepository.createUser(user); }
    public User updateUser(User user) { return userRepository.updateUser(user); }
    public long countMembers() { return userRepository.countByRole(UserRole.MEMBER); }
    public long countTrainers() { return userRepository.countByRole(UserRole.TRAINER); }

    public List<User> getUsersByRole(String roleString) {
        try {
            UserRole role = UserRole.valueOf(roleString.toUpperCase());
            return userRepository.findByRole(role);
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: An invalid role was requested: " + roleString);
            return List.of();
        }
    }
}