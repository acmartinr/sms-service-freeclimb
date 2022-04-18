package services.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import common.Utils;
import model.YtelCommonResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.Chat;
import services.database.model.Transaction;
import services.database.model.User;
import services.sms.model.PhoneConfiguration;
import services.sms.model.SMSApiMessage;
import services.sms.model.SMSApiPhone;
import services.sms.model.SMSApiResponse;
import services.sms.model.v3.SMSServiceResponseSpecial;
import services.sms.model.v3.SMSServiceV3ApiResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static common.Utils.formatPhone;

public class YtelSMSApiService implements ISMSApiService {
    final Logger logger = LoggerFactory.getLogger("access");
    // YTEL API v3
    private static String USERNAME = "a6fa649a-2e0a-4a40-9110-f6867e4a1dbf";
    private static String PASSWORD = "375dd340db3b11e9ac75dda1ee1e8847";
    private static String apiAccessToken = "";
    private static String apiRefreshToken = "";

    private static String REAL_USERNAME = "brandon@firstrateleads.com";
    private static String REAL_PASSWORD = "Durban031!!";

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    @Inject
    public YtelSMSApiService(CampaignDAO campaignDAO,
                             UserDAO userDAO,
                             SettingsDAO settingsDAO) {
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
    }

    private Response sendPostRequest(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("authorization", "Bearer " + getApiAccessToken())
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendPutRequest(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("authorization", "Bearer " + getApiAccessToken())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendLoginRequest(String content) throws Exception {
        String url = "https://api.ytel.com/auth/v2/token/";
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendGetRequest(String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("authorization", "Bearer " + getApiAccessToken())
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        return client.newCall(request).execute();
    }

    @Override
    public String getCarrierInfo(Long phone) {
        AtomicReference<String> carrier = new AtomicReference<>("");
        try {
            Response response = sendGetRequest("https://api.ytel.com/api/v4/carrier/lookup/%2B" + formatPhone(phone) + "/");
            String stringBody = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            SMSApiResponse mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);
            System.out.println("Testing #getCarrierInfo " + stringBody);

            if (mappedResponse.getStatus()) {
                mappedResponse.getPayload().forEach(phoneElement -> {
                    if (phoneElement.has("network")) {
                        String currentNetwork = phoneElement.get("network").asText();
                        if (!currentNetwork.isEmpty())
                            carrier.set(currentNetwork);
                    }
                });

                System.out.println("#getCarrierInfo response " + stringBody);

            } else {
                mappedResponse.getError().forEach(ytelV4SmsApiError -> logger.error("Error in #getCarrierInfo phone: " + phone + " Message: " + ytelV4SmsApiError.getMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carrier.get();
    }

    @Override
    public void outboundMessageStatus(Map<String, String[]> data) {
        String messageSid = data.get("MessageSid")[0].replaceAll("\u0000", "");
        String messageStatus = data.get("messageStatus")[0].replaceAll("\u0000", "");

        logger.info("Ytel message outbound SID: " + messageSid + ", Status:" + messageStatus);
    }

    @Override
    public Chat parseInboundMessage(Map<String, String[]> data) {
//        Map<String, String[]> data = request.body().asFormUrlEncoded();
        //AVOIDING NULLS IN TEXT --> ENCODING ERROR
        Chat message = new Chat();
        message.setLastMessage(data.get("Text")[0].replaceAll("\u0000", ""));
        message.setLastDate(Long.parseLong(data.get("DateSent")[0].replaceAll("\u0000", "")));
        message.setPhoneFrom(Long.parseLong(data.get("From")[0].replace("+", "")));
        message.setPhoneTo(Long.parseLong(data.get("To")[0].replace("+", "")));
        message.setLastMessageSid(data.get("MessageSid")[0].replaceAll("\u0000", ""));

        return message;
    }

    public String getApiAccessToken() {
        return apiAccessToken;
    }

    public void setApiAccessToken(String apiAccessToken) {
        YtelSMSApiService.apiAccessToken = apiAccessToken;
    }

    public String getApiRefreshToken() {
        return apiRefreshToken;
    }

    public void setApiRefreshToken(String apiRefreshToken) {
        YtelSMSApiService.apiRefreshToken = apiRefreshToken;
    }

    public synchronized void getBearerTokenByAuthentication() {
        try {
            Response response = sendLoginRequest(
                    "{\"grantType\":\"resource_owner_credentials\",\"refreshDurationMinutes\":\"30\",\"password\":\"Durban031!!\",\"username\":\"brandon@firstrateleads.com\"}");
            String body = response.body().string();

            logger.info(body);

            if (response.code() == 200) {
                JsonParser parser = new JsonParser();
                JsonElement rootElement = parser.parse(body);

                JsonObject rootObject = rootElement.getAsJsonObject();
                String accessToken = rootObject.get("accessToken").getAsString();
                if (!accessToken.isEmpty()) {
                    setApiAccessToken(accessToken);
                }
                String refreshToken = rootObject.get("refreshToken").getAsString();
                if (!refreshToken.isEmpty()) {
                    setApiRefreshToken(refreshToken);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void getBearerTokenByRefreshToken() {
        try {
            Response response = sendLoginRequest(
                    "{\"grantType\":\"refresh_token\",\"refreshDurationMinutes\":\"30\",\"refreshToken\": \"" + getApiRefreshToken() + "+\"}");
            String body = response.body().string();

            logger.info(body);

            if (response.code() == 200) {
                JsonParser parser = new JsonParser();
                JsonElement rootElement = parser.parse(body);

                JsonObject rootObject = rootElement.getAsJsonObject();
                String accessToken = rootObject.get("accessToken").getAsString();
                if (!accessToken.isEmpty()) {
                    setApiAccessToken(accessToken);
                }
                String refreshToken = rootObject.get("refreshToken").getAsString();
                if (!refreshToken.isEmpty()) {
                    setApiRefreshToken(refreshToken);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendSMS(Long phoneTo, Long phoneFrom, String message, long userId) {
        YtelCommonResponse response = sendSMS(phoneTo, phoneFrom, message, false);
        if (response.getMessageSid() != null && userId != -1) {
            scheduleRequestingSMSDetails(response.getMessageSid(), userId, response.getSentDate());
        }

        return response.getErrorMessage();
    }

    private void scheduleRequestingSMSDetails(String messageSid, long userId, Date sentDate) {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestSMSDetails(messageSid, userId, sentDate);
    }

    private void requestSMSDetails(String messageSid, long userId, Date sentDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String content = "{smsSid}/{date}/";
            content = content.
                    replace("{smsSid}", messageSid).
                    replace("{date}", dateFormat.format(sentDate));

            logger.info("Trying #requestSMSDetails request " + content);

            Response response = sendGetRequest("https://api.ytel.com/api/v4/logs/sms/details/" + content);

            if (response.code() == 200) {
                String body = response.body().string();
                String stringBody = body.replaceAll("\\n", "");

                logger.info("Trying #requestSMSDetails response " + stringBody);

                YtelCommonResponse responseMessage = parseResponseMessage(stringBody);
                if (responseMessage.getCarrierSurcharge() > 0) {
                    float price = responseMessage.getCarrierSurcharge();
                    float factor = settingsDAO.getSurchargeFactor(userId, User.DEFAULT_RESELLER_ID);

                    if (factor >= 1) {
                        price *= factor;
                    }
                    userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.CARRIER_SURCHARGE, userId, -price, -price);
                    userDAO.updateCountTransaction(Transaction.CARRIER_SURCHARGE, userId, -price, -price, "count: ");
                }
            } else {
                logger.info(response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private YtelCommonResponse sendSMS(Long phoneTo, Long phoneFrom, String message, boolean repeated) {
        try {

            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("from", "+" + formatPhone(phoneFrom).toString());
            contentMap.put("to", "+" + formatPhone(phoneTo).toString());
            contentMap.put("text", message);
            contentMap.put("deliveryStatusEnabled", "false");
            //TODO IMPLEMENT CALLBACK WHEN DELIVERED
            contentMap.put("messageStatusCallback", "");
            String content = new ObjectMapper().writeValueAsString(contentMap);

            System.out.println(content);
            logger.info("Trying #sendSMS request: " + content);

            Response response = sendPostRequest(content, "https://api.ytel.com/api/v4/sms/");
            if (response.code() == 200) {
                String body = response.body().string();
                String stringBody = body.replaceAll("\\n", "");

                logger.info("Trying #sendSMS response: " + stringBody);

                YtelCommonResponse responseMessage = parseResponseMessage(stringBody);
                String error = responseMessage.getErrorMessage();
                if (error != null && !repeated) {
                    System.out.println("Error #sending " + error);
                    return repeatIfNeeded(false, phoneTo, phoneFrom, message);
                } else {
                    return responseMessage;
                }
            } else {
                return new YtelCommonResponse(null, response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());

            return repeatIfNeeded(repeated, phoneTo, phoneFrom, message);
        }
    }

    private YtelCommonResponse repeatIfNeeded(boolean repeated, Long phoneTo, Long phoneFrom, String message) {
        if (!repeated) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sendSMS(phoneTo, phoneFrom, message, true);
        }

        return null;
    }

    public YtelCommonResponse parseResponseMessage(String stringBody) {
        try {
            logger.debug("Parsing usual response: " + stringBody);
            return parseResponse(stringBody);
        } catch (Exception e) {
            try {
                logger.debug("Parsing special response: " + stringBody);
                return parseSpecialResponse(stringBody);
            } catch (Exception e1) {
            }
        }

        logger.debug("Response parsing failed");
        return new YtelCommonResponse();
    }

    private YtelCommonResponse parseResponse(String stringBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SMSApiResponse mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);

        if (!mappedResponse.getStatus()) {
            StringBuilder errorMessage = new StringBuilder("");

            mappedResponse.getError().forEach(error -> {
                errorMessage.append(error.getMessage());
            });

            logger.error("Error in #parseResponse stringBody: " + stringBody);
            return new YtelCommonResponse(null, errorMessage.toString());

        } else {
            YtelCommonResponse result = new YtelCommonResponse();
            mappedResponse.getPayload().forEach(messageResponse -> {
                if (messageResponse.has("smsSid") || messageResponse.has("messageSid")) {
                    if (messageResponse.has("smsSid")) {
                        String smsSid = messageResponse.get("smsSid").asText();
                        result.setMessageSid(smsSid);
                    } else if (messageResponse.has("messageSid")) {
                        String smsSid = messageResponse.get("messageSid").asText();
                        result.setMessageSid(smsSid);
                    }
                    result.setErrorMessage(null);
                    if (messageResponse.has("surcharge")) {
                        float surcharge = (float) messageResponse.get("surcharge").asDouble();
                        result.setCarrierSurcharge(surcharge);
                        logger.info("CarrierSurcharge : " + surcharge);
                    }
                    if (messageResponse.has("date")) {
                        Date sentDate = new Date(messageResponse.get("date").asLong());
                        result.setSentDate(sentDate);
                    } else if (messageResponse.has("scheduledTm")) {
                        Date sentDate = new Date(messageResponse.get("scheduledTm").asLong() * 1000L);
                        result.setSentDate(sentDate);
                    }
                }
            });
            return result;
        }
    }

    public List<SMSApiPhone> searchPhones(List<String> areaCodes, List<String> strCurrentPhones) {
        List<SMSApiPhone> results = new LinkedList();

        int defaultSize = 10;

        boolean isAllAreaCodeOption = (areaCodes.size() == 1 && areaCodes.get(0).equals("all"));
        List<String> searchedAreaCodes = new ArrayList<>();

        try {
            do {
                if (isAllAreaCodeOption) {
                    areaCodes.clear();
                    while (areaCodes.size() < defaultSize && searchedAreaCodes.size() < Utils.getUsaAreaCodeSize()) {
                        String newAreaCode = Utils.getRandomUsaAreaCode();
                        if (!searchedAreaCodes.contains(newAreaCode)) {
                            areaCodes.add(newAreaCode);
                            searchedAreaCodes.add(newAreaCode);
                        }
                    }
                }

                String areaCodesStr = areaCodes.toString().replace("[", "")
                        .replace("]", "")
                        .replace(" ", "");

                String content = "?areaCode={areaCode}&offset=0&size={size}&type=sms&includeFeatures=sms";
                content = content.
                        replace("{areaCode}", areaCodesStr).
                        replace("{size}", Integer.toString(defaultSize));

                System.out.println("Sending search phones request " + content);

                Response response = sendGetRequest("https://api.ytel.com/api/v4/number/available/" + content);
                if (response.code() == 200) {
                    String stringBody = response.body().string();
                    ObjectMapper mapper = new ObjectMapper();
                    SMSApiResponse mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);

                    System.out.println("Testing #searchPhones " + stringBody);

                    if (mappedResponse.getStatus()) {
                        mappedResponse.getPayload().forEach(phoneElement -> {
                            if (phoneElement.has("phoneNumber")) {
                                String currentPhone = phoneElement.get("phoneNumber").asText();
                                if (!strCurrentPhones.contains(currentPhone.substring(1))) {
                                    SMSApiPhone phone = new SMSApiPhone();
                                    phone.setPhoneNumber(currentPhone);
                                    if (phoneElement.has("region"))
                                        phone.setRegion(phoneElement.get("region").asText());
                                    results.add(phone);
                                    strCurrentPhones.add(currentPhone.substring(1));
                                }
                            }
                        });
                    } else {
                        mappedResponse.getError().forEach(ytelV4SmsApiError -> logger.error("Error in #searchPhones Message: " + ytelV4SmsApiError.getMessage()));
                    }
                }
                System.out.println("searchedAreaCodes " + searchedAreaCodes.size());
                System.out.println("results " + results.size());
            } while (results.size() < defaultSize && isAllAreaCodeOption && searchedAreaCodes.size() < Utils.getUsaAreaCodeSize());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(results.size());
        }

        return prepareRandomResults(results);
    }

    private List<SMSApiPhone> prepareRandomResults(List<SMSApiPhone> phones) {
        Collections.shuffle(phones);
        if (phones.size() > 10) {
            phones = phones.subList(0, 10);
        }
        return phones;
    }

    public List<String> buyPhones(List<String> phones) {
        List<String> boughtPhones = new LinkedList();
        try {

            Map<String, List<String>> contentMap = new HashMap<>();
            contentMap.put("phoneNumber", phones);
            String content = new ObjectMapper().writeValueAsString(contentMap);

            Response response = sendPostRequest(content, "https://api.ytel.com/api/v4/number/purchase/");
            if (response.code() == 200) {
                String stringBody = response.body().string();

                logger.info(stringBody);

                ObjectMapper mapper = new ObjectMapper();
                SMSApiResponse mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);

                if (mappedResponse.getStatus()) {
                    mappedResponse.getPayload().forEach(phoneElement -> {
                        if (phoneElement.has("phoneNumber")) {
                            String currentPhone = phoneElement.get("phoneNumber").asText();
                            boughtPhones.add(currentPhone);
                        }
                    });
                } else {
                    mappedResponse.getError().forEach(ytelV4SmsApiError -> logger.error("Error in #buyPhones Phones: " + phones + " Message:" + ytelV4SmsApiError.getMessage()));
                }

                for (String phone : boughtPhones) {
                    String config = mapper.writeValueAsString(new PhoneConfiguration());
                    response = sendPutRequest(config, "https://api.ytel.com/api/v4/number/" + phone);
                    stringBody = response.body().string();
                    logger.info(stringBody);

                    mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);

                    if (!mappedResponse.getStatus()) {
                        mappedResponse.getError().forEach(ytelV4SmsApiError -> logger.error("Error in #buyPhones #PhoneConfigRequest Phone: " + phone + " Message:" + ytelV4SmsApiError.getMessage()));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return boughtPhones;
    }

    public boolean releasePhone(Long phone) {
        AtomicBoolean released = new AtomicBoolean(false);
        try {
            Map<String, List<String>> contentMap = new HashMap<>();
            contentMap.put("phoneNumber", Collections.singletonList("+" + phone));
            String content = new ObjectMapper().writeValueAsString(contentMap);

            logger.info("Trying #releasePhone request: " + content);

            Response response = sendPostRequest(content, "https://api.ytel.com/api/v4/number/release/");
            if (response.code() == 200) {
                String stringBody = response.body().string();
                logger.info("Trying #releasePhone response: " + stringBody);

                ObjectMapper mapper = new ObjectMapper();
                SMSApiResponse mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);

                if (mappedResponse.getStatus()) {
                    mappedResponse.getPayload().forEach(phoneElement -> {
                        if (phoneElement.has("phoneNumber")) {
                            String currentPhone = phoneElement.get("phoneNumber").asText();
                            if (currentPhone.contains(String.valueOf(phone))) {
                                released.set(true);
                                logger.info(currentPhone + " RELEASED!");
                            }
                        }
                    });
                } else {
                    mappedResponse.getError().forEach(ytelV4SmsApiError -> logger.error("Error in #releasePhone phone: " + phone + " Message:" + ytelV4SmsApiError.getMessage()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return released.get();
    }

    // TODO V3 DEPRECATED METHODS

    private Response sendV3Request(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.authenticator((route, response) -> {
            String credential = Credentials.basic(USERNAME, PASSWORD);
            return response.request().newBuilder().header("Authorization", credential).build();
        });

        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        return client.newCall(request).execute();
    }

    public YtelCommonResponse parseV3ApiResponseMessage(String stringBody) {
        try {
            logger.debug("Parsing usual response: " + stringBody);
            return parseV3ApiResponse(stringBody);
        } catch (Exception e) {
            try {
                logger.debug("Parsing special response: " + stringBody);
                return parseSpecialResponse(stringBody);
            } catch (Exception e1) {
            }
        }

        logger.debug("Response parsing failed");
        return new YtelCommonResponse();
    }

    // TODO DELETE OLD VERSION PARSE
    private YtelCommonResponse parseSpecialResponse(String stringBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SMSServiceResponseSpecial response = mapper.readValue(stringBody, SMSServiceResponseSpecial.class);

        if (response != null && response.getMessage360() != null &&
                response.getMessage360().getErrors() != null &&
                response.getMessage360().getErrors() != null &&
                response.getMessage360().getErrors().size() > 0) {
            String errorMessage = response.getMessage360().getErrors().get(0).getMessage();
            logger.info(errorMessage);

            return new YtelCommonResponse(null, errorMessage);
        }

        if (response != null && response.getMessage360() != null &&
                response.getMessage360().getMessage() != null) {
            YtelCommonResponse result = new YtelCommonResponse(response.getMessage360().getMessage().getMessageSid(), null);
            if ("Verizon Wireless".equalsIgnoreCase(response.getMessage360().getMessage().getCarrier())) {
                result.setCarrierSurcharge(0.0026f);
            }
            if (response.getMessage360().getMessage().getCarrierSurcharge() > 0) {
                result.setCarrierSurcharge(response.getMessage360().getMessage().getCarrierSurcharge());
            }

            return result;
        }

        return new YtelCommonResponse();
    }

    // TODO DELETE OLD VERSION PARSE
    private YtelCommonResponse parseV3ApiResponse(String stringBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SMSServiceV3ApiResponse response = mapper.readValue(stringBody, SMSServiceV3ApiResponse.class);

        if (response != null && response.getMessage360() != null &&
                response.getMessage360().getErrors() != null &&
                response.getMessage360().getErrors().getError() != null &&
                response.getMessage360().getErrors().getError().size() > 0) {
            String errorMessage = response.getMessage360().getErrors().getError().get(0).getMessage();
            logger.info(errorMessage);

            return new YtelCommonResponse(null, errorMessage);
        }

        if (response != null && response.getMessage360() != null &&
                response.getMessage360().getMessage() != null) {
            YtelCommonResponse result = new YtelCommonResponse(response.getMessage360().getMessage().getMessageSid(), null);
            logger.info("CarrierSurcharge : " + response.getMessage360().getMessage().getCarrierSurcharge());
            if (response.getMessage360().getMessage().getCarrierSurcharge() > 0) {
                result.setCarrierSurcharge(response.getMessage360().getMessage().getCarrierSurcharge());
            }

            return result;
        }

        return new YtelCommonResponse();
    }

    private String generateAreaCodesString(List<String> areaCodes) {
        StringBuilder result = new StringBuilder();
        for (String areaCode : areaCodes) {
            result.append("areaCode=").append(areaCode).append("&");
        }

        return result.toString();
    }

    private List<SMSApiPhone> parsePhones(String stringBody) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SMSServiceV3ApiResponse response = mapper.readValue(stringBody, SMSServiceV3ApiResponse.class);

        if (response != null && response.getMessage360().getPhones() != null &&
                response.getMessage360().getPhones().getPhone() != null) {
            return response.getMessage360().getPhones().getPhone();
        }

        return new LinkedList();
    }

    public void requestInboundMessages(String userId) {
        List<Chat> chats = campaignDAO.getAllChats();
        for (Chat chat : chats) {
            requestInboundMessage(chat);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<SMSApiMessage> parseMessages(String stringBody) throws Exception {
        List<SMSApiMessage> messages = new LinkedList();

        ObjectMapper mapper = new ObjectMapper();
        SMSServiceV3ApiResponse response = mapper.readValue(stringBody, SMSServiceV3ApiResponse.class);
        if (response.getMessage360() != null && response.getMessage360().getMessages() != null &&
                response.getMessage360().getMessages().getMessage() != null) {
            messages = response.getMessage360().getMessages().getMessage();
        }

        return messages;
    }

    // TODO DROP DEPRECATED FUNCTION IN V4
    private void requestInboundMessage(Chat chat) {
        try {
//            String content = "Page=1&PageSize=10&From=+{from}&To=&DateSent={date}";
            String content = "?startDate=2021-01-09&endDate=2021-01-10&limit=1";
            content = content
                    .replace("{startDate}", formatPhone(chat.getPhoneTo()).toString())
                    .replace("{date}", dateFormat.format(new Date()));

            logger.info("Entering deprecated function Content: " + content);

            Response response = sendGetRequest("https://api.ytel.com/api/v4/logs/sms/" + content);
            if (response.code() == 200) {
                String body = response.body().string();
                String stringBody = body.replaceAll("\\n", "");
                //Logger.info(body);

                if (parseV3ApiResponseMessage(stringBody).getErrorMessage() == null) {
                    List<SMSApiMessage> messages = parseMessages(stringBody);
                    for (SMSApiMessage message : messages) {
                        campaignDAO.updateExternalChatMessage(chat, message);
                    }

                    if (messages.size() > 0) {
                        campaignDAO.updateHasInboundForChat(chat.getId(), chat.getUserId(), true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
