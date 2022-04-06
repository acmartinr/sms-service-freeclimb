package services.database.model;

public class User {

    public static final int ADMIN = 0;
    public static final int REGULAR = 1;
    public static final int LIMITED = 2;
    public static final int RESELLER = 3;

    public static final int DEFAULT_RESELLER_ID = 1;

    public static final int ORIGIN_TEXTALLDATA = 0;
    public static final int ORIGIN_APP2 = 1;

    private long id;
    private String username;
    private int role;
    private int origin;
    private String email;
    private String password;
    private long date;
    private float balance;
    private String fullName;
    private long resellerId;
    private long resellerNumber;
    private String personalName;
    private String domain;
    private boolean allowManageMoney;
    private boolean allowSubUsersPayments;
    private boolean allowSubUsersTransactionsView;
    private boolean allowPayments;
    private boolean allowTransactionsView;
    private boolean blocked;
    private boolean disabled;

    private String autoReplyEnabled;
    private String agentLoginEnabled;
    private long adminUserId;

    private long lastCampaignDate;

    private String timezoneName;
    private int timezoneOffset;

    public User() {}

    public User(String username,
                int role,
                int origin,
                String email,
                String password,
                long date,
                float balance,
                String fullName,
                long resellerId) {
        this.username = username;
        this.role = role;
        this.origin = origin;
        this.email = email;
        this.password = password;
        this.date = date;
        this.balance = balance;
        this.fullName = fullName;
        this.resellerId = resellerId;
    }

    public User(String username, String password, int role, float balance, long adminUserId, long resellerId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = balance;
        this.adminUserId = adminUserId;
        this.resellerId = resellerId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAutoReplyEnabled() {
        return autoReplyEnabled;
    }

    public void setAutoReplyEnabled(String autoReplyEnabled) {
        this.autoReplyEnabled = autoReplyEnabled;
    }

    public String getAgentLoginEnabled() {
        return agentLoginEnabled;
    }

    public void setAgentLoginEnabled(String agentLoginEnabled) {
        this.agentLoginEnabled = agentLoginEnabled;
    }

    public long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public long getResellerId() {
        return resellerId;
    }

    public void setResellerId(long resellerId) {
        this.resellerId = resellerId;
    }

    public long getResellerNumber() {
        return resellerNumber;
    }

    public void setResellerNumber(long resellerNumber) {
        this.resellerNumber = resellerNumber;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getLastCampaignDate() {
        return lastCampaignDate;
    }

    public void setLastCampaignDate(long lastCampaignDate) {
        this.lastCampaignDate = lastCampaignDate;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public boolean isAllowManageMoney() {
        return allowManageMoney;
    }

    public void setAllowManageMoney(boolean allowManageMoney) {
        this.allowManageMoney = allowManageMoney;
    }

    public boolean isAllowSubUsersTransactionsView() {
        return allowSubUsersTransactionsView;
    }

    public void setAllowSubUsersTransactionsView(boolean allowSubUsersTransactionsView) {
        this.allowSubUsersTransactionsView = allowSubUsersTransactionsView;
    }

    public boolean isAllowSubUsersPayments() {
        return allowSubUsersPayments;
    }

    public void setAllowSubUsersPayments(boolean allowSubUsersPayments) {
        this.allowSubUsersPayments = allowSubUsersPayments;
    }

    public boolean isAllowTransactionsView() {
        return allowTransactionsView;
    }

    public void setAllowTransactionsView(boolean allowTransactionsView) {
        this.allowTransactionsView = allowTransactionsView;
    }

    public boolean isAllowPayments() {
        return allowPayments;
    }

    public void setAllowPayments(boolean allowPayments) {
        this.allowPayments = allowPayments;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getTimezoneName() {
        return timezoneName;
    }

    public void setTimezoneName(String timezoneName) {
        this.timezoneName = timezoneName;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }
}
