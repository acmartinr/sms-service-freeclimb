package services.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.*;
import com.google.inject.Inject;
import common.Utils;
import okhttp3.*;
import play.Logger;
import play.mvc.Http;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.Chat;
import services.sms.model.SMSApiPhone;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TelnexSMSApiService implements ISMSApiService {

    private static final String API_KEY = "jIKzR38fTz-XHmii66B-sg";
    private static final String API_USER = "contact@textalldata.com";

    private static final String MESSAGING_PROFILE_KEY = "ki0xFTknh2aqO7VQfoivSfwG";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    @Inject
    public TelnexSMSApiService(CampaignDAO campaignDAO,
                               UserDAO userDAO,
                               SettingsDAO settingsDAO) {
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
    }

    private Response sendPostRequest(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.build();

        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("x-api-token", API_KEY)
                .addHeader("x-api-user", API_USER)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendPatchRequest(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.build();

        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .addHeader("x-api-token", API_KEY)
                .addHeader("x-api-user", API_USER)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendPostMessagingRequest(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.build();

        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("X-Profile-Secret", MESSAGING_PROFILE_KEY)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendDeleteRequest(String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("x-api-token", API_KEY)
                .addHeader("x-api-user", API_USER)
                .build();

        return client.newCall(request).execute();
    }

    private Response sendGetRequest(String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        OkHttpClient client = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", API_KEY)
                .build();

        return client.newCall(request).execute();
    }

    @Override
    public List<String> buyPhones(List<String> phones) {
        try {
            Response response = sendPostRequest(generateBuyPhonesRequestBody(phones).toString(), "https://api.telnyx.com/origination/number_orders");
            Logger.info(response.body().string());
            if (response.code() == 200) {
                //Thread.sleep(2000);

                //updatePhonesMessagingProfile(phones);
                return phones;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new LinkedList();
    }

    private void updatePhonesMessagingProfile(List<String> phones) {
        for (String phone : phones) {
            try {
                Response response = sendPatchRequest(
                        generateUpdatePhoneRequestBody().toString(),
                        "https://api.telnyx.com/messaging/numbers/" + phone.replace("+", ""));
                Logger.info(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JsonObject generateUpdatePhoneRequestBody() {
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("profile_id", "77776cc9-f0a7-4c95-80b8-15ff5086e3c9");

        return requestObject;
    }

    private JsonObject generateBuyPhonesRequestBody(List<String> phones) {
        JsonObject requestObject = new JsonObject();

        JsonArray phonesArray = new JsonArray();
        for (String phone : phones) {
            phonesArray.add(new JsonPrimitive(phone));
        }

        requestObject.add("requested_numbers", phonesArray);
        requestObject.add("messaging_profile_id", new JsonPrimitive("77776cc9-f0a7-4c95-80b8-15ff5086e3c9"));

        return requestObject;
    }

    @Override
    public List<SMSApiPhone> searchPhones(List<String> areaCodes, List<String> strCurrentPhone) {
        //TODO filter current bought phones "strCurrentPhone"
        try {
            Response response = sendPostRequest(generateSearchPhoneRequestBody(areaCodes).toString(), "https://api.telnyx.com/origination/number_searches");

            String body = response.body().string();
            Logger.info(body);

            if (response.code() == 200) {
                return parseResults(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new LinkedList();
    }

    private List<SMSApiPhone> parseResults(String body) {
        List<SMSApiPhone> results = new LinkedList();

        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(body);

        JsonObject rootObject = rootElement.getAsJsonObject();

        JsonElement resultsElement = rootObject.get("result");
        JsonArray resultsArray = resultsElement.getAsJsonArray();

        for (int i = 0; i < resultsArray.size(); i++) {
            JsonElement phoneElement = resultsArray.get(i);
            JsonObject phoneObject = phoneElement.getAsJsonObject();

            String phone = phoneObject.get("number_e164").getAsString();
            results.add(new SMSApiPhone(phone));
        }

        return results;
    }

    private JsonObject generateSearchPhoneRequestBody(List<String> areaCodes) {
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("search_type", 4);
        requestObject.addProperty("limit", 10);

        JsonObject searchDescriptor = new JsonObject();
        searchDescriptor.addProperty("npa", areaCodes.get(0));

        JsonArray features = new JsonArray();
        features.add(new JsonPrimitive("sms"));
        searchDescriptor.add("has_all_features", features);

        requestObject.add("search_descriptor", searchDescriptor);

        return requestObject;
    }

    @Override
    public boolean releasePhone(Long phone) {
        try {
            Response response = sendDeleteRequest("https://api.telnyx.com/origination/numbers/+" + phone);
            Logger.info(response.body().string());

            if (response.code() == 200) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getCarrierInfo(Long phone) {
        try {
            Response response = sendGetRequest("https://lrnlookup.telnyx.com/v1.1/LRNLookup/" + formatPhone(phone));
            String body = response.body().string();

            System.out.println(body);
            Logger.info(body);

            if (response.code() == 200) {
                JsonParser parser = new JsonParser();
                JsonElement rootElement = parser.parse(body);

                JsonObject rootObject = rootElement.getAsJsonObject();
                String carrier = rootObject.get("spid_carrier_name").getAsString();

                return carrier != null ? carrier : "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private String formatPhone(Long phone) {
        String strPhone = phone.toString();
        if (strPhone.startsWith("1")) {
            strPhone = strPhone.substring(1);
        }

        return strPhone;
    }

    @Override
    public String sendSMS(Long phoneTo, Long phoneFrom, String message, long userId) {
        return sendSMS(phoneTo, phoneFrom, message, false);
    }


    private String sendSMS(Long phoneTo, Long phoneFrom, String message, boolean repeated) {
        try {
            Response response = sendPostMessagingRequest(generateSendMessageRequest(phoneTo, phoneFrom, message).toString(), "https://sms.telnyx.com/messages");
            String body = response.body().string();
            Logger.info(body);

            if (response.code() == 200) {
                return null;
            } else {
                JsonParser parser = new JsonParser();
                JsonElement rootElement = parser.parse(body);

                JsonObject rootObject = rootElement.getAsJsonObject();
                if (!rootObject.get("success").getAsBoolean()) {
                    return rootObject.get("message").getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            return repeatIfNeeded(repeated, phoneFrom, phoneTo, message);
        }

        return "Sending SMS common error";
    }

    private JsonObject generateSendMessageRequest(Long phoneTo, Long phoneFrom, String message) {
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("from", "+" + Utils.formatPhone(phoneFrom));
        requestObject.addProperty("to", "+" + Utils.formatPhone(phoneTo));
        requestObject.addProperty("body", message);

        return requestObject;
    }

    private String repeatIfNeeded(boolean repeated, Long phoneFrom, Long phoneTo, String message) {
        if (!repeated) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sendSMS(phoneFrom, phoneTo, message, true);
        }

        return null;
    }

    @Override
    public void requestInboundMessages(String userId) {

    }

    @Override
    public void outboundMessageStatus(Map<String, String[]> data) {
        String messageSid = data.get("MessageSid")[0].replaceAll("\u0000", "");
        String messageStatus = data.get("messageStatus")[0].replaceAll("\u0000", "");

        System.out.println("Getting TelnexSMS #outboundMessageStatus SID: " + messageSid + ", Status:" + messageStatus);

    }

    @Override
    public Chat parseInboundMessage(Map<String, String[]> data) {
        return null;
    }
}
