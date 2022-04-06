package model;

public class KycInfo {

    private long id;
    private long userId;
    private String companyName;
    private String brandName;
    private String companyCountry;
    private String companyTaxId;
    private int businessTypeId;
    private String companyWebsite;
    private String companyStockExchange;
    private String companyAddressStreet;
    private String companyAddressZip;
    private String companyAddressCity;
    private String companyAddressState;
    private String companyAddressCountry;
    private String contactFirstName;
    private String contactLastName;
    private String contactPhone;
    private String contactEmail;
    private String billingEmail;
    private String contactTollFreePhone;
    private String supportEmail;
    private long lastUpdate;
    private String businessPosition;
    private String businessTitle;
    private boolean confirmedTerms;
    private boolean confirmedProviderContact;
    private int businessIndustryId;

    private int useCaseId;
    private String relevantAttributesIds;
    private String campaignDescription;
    private String messageSample;

    private boolean completed;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void updateKycInfo(KycInfo update) {
        if (update.companyName != null && !update.companyName.isEmpty()) {
            this.companyName = update.companyName;
            this.businessTypeId = update.businessTypeId;
            this.brandName = update.brandName;
            this.companyCountry = update.companyCountry;
            this.companyWebsite = update.companyWebsite;
            this.companyTaxId = update.companyTaxId;
            this.companyStockExchange = update.companyStockExchange;
            this.companyAddressStreet = update.companyAddressStreet;
            this.companyAddressZip = update.companyAddressZip;
            this.companyAddressCity = update.companyAddressCity;
            this.companyAddressState = update.companyAddressState;
            this.companyAddressCountry = update.companyAddressCountry;
            this.contactFirstName = update.contactFirstName;
            this.contactLastName = update.contactLastName;
            this.contactPhone = update.contactPhone;
            this.contactEmail = update.contactEmail;
            this.confirmedProviderContact = update.confirmedProviderContact;
            this.billingEmail = update.billingEmail;
            this.contactTollFreePhone = update.contactTollFreePhone;
            this.supportEmail = update.supportEmail;
            this.lastUpdate = update.lastUpdate;
            this.businessPosition = update.businessPosition;
            this.businessTitle = update.businessTitle;
            this.confirmedTerms = update.confirmedTerms;
            this.businessIndustryId = update.businessIndustryId;
        }
        if (update.campaignDescription != null && !update.campaignDescription.isEmpty()) {
            this.useCaseId = update.useCaseId;
            this.relevantAttributesIds = update.relevantAttributesIds;
            this.campaignDescription = update.campaignDescription;
            this.messageSample = update.messageSample;
        }
        if (update.completed)
            this.completed = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }

    public String getCompanyTaxId() {
        return companyTaxId;
    }

    public void setCompanyTaxId(String companyTaxId) {
        this.companyTaxId = companyTaxId;
    }

    public int getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(int businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyStockExchange() {
        return companyStockExchange;
    }

    public void setCompanyStockExchange(String companyStockExchange) {
        this.companyStockExchange = companyStockExchange;
    }

    public String getCompanyAddressStreet() {
        return companyAddressStreet;
    }

    public void setCompanyAddressStreet(String companyAddressStreet) {
        this.companyAddressStreet = companyAddressStreet;
    }

    public String getCompanyAddressZip() {
        return companyAddressZip;
    }

    public void setCompanyAddressZip(String companyAddressZip) {
        this.companyAddressZip = companyAddressZip;
    }

    public String getCompanyAddressCity() {
        return companyAddressCity;
    }

    public void setCompanyAddressCity(String companyAddressCity) {
        this.companyAddressCity = companyAddressCity;
    }

    public String getCompanyAddressState() {
        return companyAddressState;
    }

    public void setCompanyAddressState(String companyAddressState) {
        this.companyAddressState = companyAddressState;
    }

    public String getCompanyAddressCountry() {
        return companyAddressCountry;
    }

    public void setCompanyAddressCountry(String companyAddressCountry) {
        this.companyAddressCountry = companyAddressCountry;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getContactTollFreePhone() {
        return contactTollFreePhone;
    }

    public void setContactTollFreePhone(String contactTollFreePhone) {
        this.contactTollFreePhone = contactTollFreePhone;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getBusinessPosition() {
        return businessPosition;
    }

    public void setBusinessPosition(String businessPosition) {
        this.businessPosition = businessPosition;
    }

    public String getBusinessTitle() {
        return businessTitle;
    }

    public void setBusinessTitle(String businessTitle) {
        this.businessTitle = businessTitle;
    }

    public boolean isConfirmedTerms() {
        return confirmedTerms;
    }

    public void setConfirmedTerms(boolean confirmedTerms) {
        this.confirmedTerms = confirmedTerms;
    }

    public boolean isConfirmedProviderContact() {
        return confirmedProviderContact;
    }

    public void setConfirmedProviderContact(boolean confirmedProviderContact) {
        this.confirmedProviderContact = confirmedProviderContact;
    }

    public int getBusinessIndustryId() {
        return businessIndustryId;
    }

    public void setBusinessIndustryId(int businessIndustryId) {
        this.businessIndustryId = businessIndustryId;
    }

    public int getUseCaseId() {
        return useCaseId;
    }

    public void setUseCaseId(int useCaseId) {
        this.useCaseId = useCaseId;
    }

    public String getRelevantAttributesIds() {
        return relevantAttributesIds;
    }

    public void setRelevantAttributesIds(String relevantAttributesIds) {
        this.relevantAttributesIds = relevantAttributesIds;
    }

    public String getCampaignDescription() {
        return campaignDescription;
    }

    public void setCampaignDescription(String campaignDescription) {
        this.campaignDescription = campaignDescription;
    }

    public String getMessageSample() {
        return messageSample;
    }

    public void setMessageSample(String messageSample) {
        this.messageSample = messageSample;
    }

    @Override
    public String toString() {
        return "KycInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", companyName='" + companyName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", companyCountry='" + companyCountry + '\'' +
                ", companyTaxId='" + companyTaxId + '\'' +
                ", businessTypeId=" + businessTypeId +
                ", companyWebsite='" + companyWebsite + '\'' +
                ", companyStockExchange='" + companyStockExchange + '\'' +
                ", companyAddressStreet='" + companyAddressStreet + '\'' +
                ", companyAddressZip='" + companyAddressZip + '\'' +
                ", companyAddressCity='" + companyAddressCity + '\'' +
                ", companyAddressState='" + companyAddressState + '\'' +
                ", companyAddressCountry='" + companyAddressCountry + '\'' +
                ", contactFirstName='" + contactFirstName + '\'' +
                ", contactLastName='" + contactLastName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", billingEmail='" + billingEmail + '\'' +
                ", contactTollFreePhone='" + contactTollFreePhone + '\'' +
                ", supportEmail='" + supportEmail + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", businessPosition='" + businessPosition + '\'' +
                ", businessTitle='" + businessTitle + '\'' +
                ", confirmedTerms=" + confirmedTerms +
                ", confirmedProviderContact=" + confirmedProviderContact +
                ", businessIndustryId=" + businessIndustryId +
                ", useCaseId=" + useCaseId +
                ", relevantAttributesIds='" + relevantAttributesIds + '\'' +
                ", campaignDescription='" + campaignDescription + '\'' +
                ", messageSample='" + messageSample + '\'' +
                '}';
    }
}
