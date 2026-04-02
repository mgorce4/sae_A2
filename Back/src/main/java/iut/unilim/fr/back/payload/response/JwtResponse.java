package iut.unilim.fr.back.payload.response;

import java.util.List;

public class JwtResponse {

    private String token;
    private final String type = "Bearer";
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private List<Integer> accessRights;
    private Long idInstitution;
    private String institutionName;
    private String institutionLocation;

    public JwtResponse(String token, Long id, String username, String firstname, String lastname,
                       List<Integer> accessRights, Long idInstitution,
                       String institutionName, String institutionLocation) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.accessRights = accessRights;
        this.idInstitution = idInstitution;
        this.institutionName = institutionName;
        this.institutionLocation = institutionLocation;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public List<Integer> getAccessRights() { return accessRights; }
    public void setAccessRights(List<Integer> accessRights) { this.accessRights = accessRights; }

    public Long getIdInstitution() { return idInstitution; }
    public void setIdInstitution(Long idInstitution) { this.idInstitution = idInstitution; }

    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }

    public String getInstitutionLocation() { return institutionLocation; }
    public void setInstitutionLocation(String institutionLocation) { this.institutionLocation = institutionLocation; }
}
