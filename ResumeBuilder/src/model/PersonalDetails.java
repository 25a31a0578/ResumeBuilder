package model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Name, contact information, links and photo. Fields are private (encapsulation). */
public class PersonalDetails {
    private String name = "";
    private String headline = "";
    private String phone = "";
    private String email = "";
    private String address = "";
    private String linkedin = "";
    private String github = "";
    private String leetcode = "";
    private String hackerrank = "";
    private String photoPath = "";

    public String getName() { return name; }
    public String getHeadline() { return headline; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getLinkedin() { return linkedin; }
    public String getGithub() { return github; }
    public String getLeetcode() { return leetcode; }
    public String getHackerrank() { return hackerrank; }
    public String getPhotoPath() { return photoPath; }

    public void setName(String v) { name = Utils.clean(v); }
    public void setHeadline(String v) { headline = Utils.clean(v); }
    public void setPhone(String v) { phone = Utils.clean(v); }
    public void setEmail(String v) { email = Utils.clean(v); }
    public void setAddress(String v) { address = Utils.clean(v); }
    public void setLinkedin(String v) { linkedin = Utils.clean(v); }
    public void setGithub(String v) { github = Utils.clean(v); }
    public void setLeetcode(String v) { leetcode = Utils.clean(v); }
    public void setHackerrank(String v) { hackerrank = Utils.clean(v); }
    public void setPhotoPath(String v) { photoPath = Utils.clean(v); }

    /** Only the links the user actually filled in (label -> url), in a fixed order. */
    public Map<String, String> getLinks() {
        Map<String, String> links = new LinkedHashMap<>();
        if (Utils.has(linkedin)) links.put("LinkedIn", linkedin);
        if (Utils.has(github)) links.put("GitHub", github);
        if (Utils.has(leetcode)) links.put("LeetCode", leetcode);
        if (Utils.has(hackerrank)) links.put("HackerRank", hackerrank);
        return links;
    }
}
