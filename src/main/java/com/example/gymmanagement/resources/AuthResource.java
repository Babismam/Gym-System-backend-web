@POST
@Path("/register")
public Response register(RegisterDTO dto) {
    if (userRepository.findByUsername(dto.getUsername()) != null) {
        return Response.status(Response.Status.CONFLICT).entity(Map.of("error", "Το όνομα χρήστη υπάρχει ήδη.")).build();
    }

    // ΜΕΤΑΤΡΟΠΗ STRING ΣΕ LOCALDATE
    LocalDate dob = null;
    if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isEmpty()) {
        dob = LocalDate.parse(dto.getDateOfBirth()); // Αυτό κάνει τη δουλειά
    }

    if (dob == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "Η ημερομηνία γέννησης είναι υποχρεωτική.")).build();
    }
    if (dob.isAfter(LocalDate.now().minusYears(18))) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "Οι χρήστες πρέπει να είναι άνω των 18 ετών.")).build();
    }

    UserRole userRole = UserRole.MEMBER;

    User newUser = new User(
            dto.getFirstName(), dto.getLastName(), dto.getEmail(),
            dto.getPhone(), dto.getPassword(), dto.getUsername(),
            userRole, dob // Περνάμε το μετατρεπόμενο αντικείμενο
    );

    if (newUser.isMember()) {
        userService.setInitialMembershipFromDTO(newUser, dto);
    }

    userService.createUser(newUser);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "User registered successfully");
    response.put("username", newUser.getUsername());
    response.put("role", newUser.getRole().name());

    return Response.status(Response.Status.CREATED).entity(response).build();
}