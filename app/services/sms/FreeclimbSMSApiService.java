package services.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import common.Utils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.dao.CampaignDAO;
import services.database.model.Chat;
import services.sms.model.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class FreeclimbSMSApiService implements ISMSApiService{
    private CampaignDAO campaignDAO;
    final Logger logger = LoggerFactory.getLogger("access");
    private static String BASE_URL = "https://www.freeclimb.com/apiserver";
    private static String ACCOUNT_SID = "AC1303985d31355c460fbbdcf7c28a5d4c484f0d48";
    private static String AUTH_TOKEN = "37b4305221be1bc55432d88037075be512d39984";


    @Inject
    public FreeclimbSMSApiService(CampaignDAO campaignDAO) {
        this.campaignDAO = campaignDAO;
//        this.userDAO = userDAO;
//        this.settingsDAO = settingsDAO;
        //  Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }
    private Response sendGetRequest(String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        OkHttpClient client = builder.build();
        String userpass = ACCOUNT_SID + ":" + AUTH_TOKEN;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("authorization", basicAuth)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        return client.newCall(request).execute();
    }

    private Response sendPostRequest(String content, String url) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("application/json");
        String userpass = ACCOUNT_SID + ":" + AUTH_TOKEN;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", basicAuth)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    @Override
    public List<String> buyPhones(List<String> phones) {
        List<String> boughtPhones = new LinkedList();
        try {

            logger.info("Sending #buyPhones request for phones: " + phones);

            for (String phone : phones) {
                Map<String, String> contentMap = new HashMap<>();
                contentMap.put("phoneNumber", phone);
                String content = new ObjectMapper().writeValueAsString(contentMap);
                Response response = sendPostRequest(content, "https://www.freeclimb.com/apiserver/Accounts/AC1303985d31355c460fbbdcf7c28a5d4c484f0d48/IncomingPhoneNumbers");
                if (response.code() == 202) {
                    String stringBody = response.body().string();
                    System.out.println(stringBody);
                    logger.info(stringBody);
                    ObjectMapper mapper = new ObjectMapper();
                    // SMSApiResponse mappedResponse = mapper.readValue(stringBody, SMSApiResponse.class);
                }else{
                    System.out.println(response.body().string());
                }
            }

            logger.info("Getting #buyPhones response boughtPhones: " + boughtPhones);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return boughtPhones;
    }

    public List<String> buyPhones_old(List<String> phones) {
        List<String> boughtPhones = new LinkedList();
        try {

            logger.info("Sending #buyPhones request for phones: " + phones);

            for (String phone : phones) {
                URL url = new URL("https://www.freeclimb.com/apiserver/Accounts/"+ACCOUNT_SID+"/IncomingPhoneNumbers");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                String userpass = ACCOUNT_SID + ":" + AUTH_TOKEN;
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
                conn.setRequestProperty("Authorization", basicAuth);
                conn.setRequestProperty("Content-Type", "application/json");

                //String input = "{\"accountId\":\""+ACCOUNT_SID+"\",\"phoneNumber\":\""+phone+"\"}";
                String input = "{\"phoneNumber\":\"+18338175757\"}";
                // String input  = "accountId="+ACCOUNT_SID+"&phoneNumber="+phone;
                // byte[] postData       = input.getBytes( StandardCharsets.UTF_8 );
                //  int    postDataLength = postData.length;
                //   conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));

                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
                boughtPhones.add(phone);
                conn.disconnect();
                /*
                IncomingPhoneNumber incomingPhoneNumber = IncomingPhoneNumber.creator(
                                new com.twilio.type.PhoneNumber(phone))
                        .setSmsUrl(URI.create("https://bizownercells.com/api/inbound"))
                        .setVoiceUrl(URI.create("https://bizownercells.com/api/forward/twilio"))
                        .setStatusCallback(URI.create("https://bizownercells.com/api/outbond/status"))
                        .create();
                */

            }

            logger.info("Getting #buyPhones response boughtPhones: " + boughtPhones);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return boughtPhones;
    }

    @Override
    public List<SMSApiPhone> searchPhones(List<String> areaCodes, List<String> strCurrentPhones) {
        int defaultSize = 10;
        int areacodePerPage = 3;
        List<SMSApiPhone> results = new LinkedList();
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
                for (String areaCode : areaCodes) {
                    logger.info("Sending #searchPhones request for areacode: " + areaCode);

                    URL url = new URL("https://www.freeclimb.com/apiserver/AvailablePhoneNumbers");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("GET");
                    String userpass = ACCOUNT_SID + ":" + AUTH_TOKEN;
                    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
                    conn.setRequestProperty("Authorization", basicAuth);
                    conn.setRequestProperty("Content-Type", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;
                    while ((output = br.readLine()) != null) {
                        Gson g = new Gson();
                        FreeclimbAvailablePhones ap = g.fromJson(output, FreeclimbAvailablePhones.class);
                        for (FreeClimbPhoneNumber phoneNumber : ap.getAvailablePhoneNumbers()) {
                            SMSApiPhone sa = new SMSApiPhone();
                            sa.setRegion(phoneNumber.getRegion());
                            sa.setPhoneNumber(phoneNumber.getPhoneNumber());
                            results.add(sa);
                        }

                    }

                    conn.disconnect();
                }

                logger.info("searchedAreaCodes " + searchedAreaCodes.size());
                logger.info("results " + results.size());

            } while (results.size() < defaultSize && isAllAreaCodeOption && searchedAreaCodes.size() < Utils.getUsaAreaCodeSize());
        } catch (Exception e) {
            e.printStackTrace();
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
    @Override
    public boolean releasePhone(Long phone) {
        boolean released = false;
        try {
            logger.info("Sending #releasePhone request for phone: " + phone);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return released;
    }

    @Override
    public String sendSMS(Long phoneTo, Long phoneFrom, String message, long userId) {
        try {
            FreeClimbSendSMSResponse response = sendSMS(phoneTo, phoneFrom, message, false);
            System.out.println(response.getMessageId());
            if (response.getMessageId() != null && userId != -1) {
                return null;
            }
            if(response.getStatus().equals("queued")){
                return null;
            }else{
                return response.getStatus();
            }

        } catch (Exception e) {
            //TODO DROP IN PRODUCTION
            return e.getMessage();
        }
    }

    @Override
    public void requestInboundMessages() {
        logger.info("Sending #requestInboundMessages request");
        List<Chat> chats = campaignDAO.getAllChats();
        System.out.println(chats.size());
        for (Chat chat : chats) {

            requestInboundMessage(chat);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getCarrierInfo(Long phone) {
        return null;
    }

    @Override
    public void outboundMessageStatus(Map<String, String[]> data) {

    }

    @Override
    public Chat parseInboundMessage(Map<String, String[]> data) {

        String messageSid = data.get("MessageSid")[0].replaceAll("\u0000", "");
        String from = data.get("From")[0].replaceAll("\u0000", "");
        String to = data.get("To")[0].replaceAll("\u0000", "");
        String body = data.get("Body")[0].replaceAll("\u0000", "");
        logger.info("messageSid " + messageSid + " from: " + from + " to: " + to + " body: " + body);

        Chat inboundMessageChat = new Chat();
        inboundMessageChat.setLastMessageSid(messageSid);
        inboundMessageChat.setLastMessage(body);
        inboundMessageChat.setPhoneFrom(formatPhoneToLong(from));
        inboundMessageChat.setPhoneTo(formatPhoneToLong(to));
//        inboundMessageChat.setLastDate(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC")).toEpochSecond());
        inboundMessageChat.setLastDate(System.currentTimeMillis());

        return inboundMessageChat;
    }


    private FreeClimbSendSMSResponse sendSMS(Long phoneTo, Long phoneFrom, String message, boolean repeated) {
        FreeClimbSendSMSResponse messageResponse = new FreeClimbSendSMSResponse();
        logger.info("Sending #sendSMS request from: " + phoneFrom + " to: " + phoneTo + " message: " + message);
        try {
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("to", formatPhone(phoneTo));
            contentMap.put("from", formatPhone(phoneFrom));
            contentMap.put("text", message);
            contentMap.put("notificationUrl", "http://dev.wsdevworld.com:9000/api/outbond/status");
            String content = new ObjectMapper().writeValueAsString(contentMap);
            Response response = sendPostRequest(content, "https://www.freeclimb.com/apiserver/Accounts/"+ACCOUNT_SID+"/Messages");
            if (response.code() == 202) {
                String stringBody = response.body().string();
                System.out.println(stringBody);
                logger.info(stringBody);
                ObjectMapper mapper = new ObjectMapper();
                messageResponse = mapper.readValue(stringBody, FreeClimbSendSMSResponse.class);
                logger.info("Message sent: " + messageResponse.getMessageId());
            }else{
                messageResponse.setMessageId(null);
                System.out.println(response.body().string());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error #sendSMS phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            logger.info("Error #sendSMS phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            if (!repeated)
                return repeatIfNeeded(repeated, phoneTo, phoneFrom, message);
            try {
                throw e;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return messageResponse;
    }


    private FreeClimbSendSMSResponse repeatIfNeeded(boolean repeated, Long phoneTo, Long phoneFrom, String message) {
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
    private void requestInboundMessage(Chat chat) {
        try {
            logger.info("Sending #requestInboundMessage request for phone from: " + chat.getPhoneFrom());
            FreeclimbSmsListResponse pl = new FreeclimbSmsListResponse();
            String content = "?to=12324323523&beginTime=2021-01-10&endTime=1&direction=inbound";
            //  contentMap.put("to", formatPhone(chat.getPhoneTo()));
            //  contentMap.put("beginTime", ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC")).toString());
            // String content = new ObjectMapper().writeValueAsString(contentMap);
            Response response = sendGetRequest("https://www.freeclimb.com/apiserver/"+ACCOUNT_SID+"/Messages/" + content);
            System.out.println("https://www.freeclimb.com/apiserver/"+ACCOUNT_SID+"/Messages/"+content);
            if (response.code() == 200) {
                String stringBody = response.body().string();
                System.out.println(stringBody);
                logger.info(stringBody);
                ObjectMapper mapper = new ObjectMapper();
                pl = mapper.readValue(stringBody, FreeclimbSmsListResponse.class);
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatPhone(Long phone) {
        if (phone == null) {
            return null;
        }
        String strPhone = phone.toString();
        if (!strPhone.startsWith("+1")) {
            if (!strPhone.startsWith("1"))
                return "+1" + strPhone;
            else return "+" + strPhone;
        }
        return strPhone;
    }
    private static Long formatPhoneToLong(String phone) {
        if (phone == null) {
            return null;
        }
        String strPhone = phone.replace("+1", "1");
        if (!strPhone.startsWith("1") && strPhone.length() == 10) {
            strPhone = "1" + phone;
        }
        return Long.parseLong(strPhone);
    }

}
