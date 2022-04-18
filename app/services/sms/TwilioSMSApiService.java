package services.sms;

import com.google.inject.Inject;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local;
import com.twilio.rest.lookups.v1.PhoneNumber;
import common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.dao.CampaignDAO;
import services.database.model.Chat;
import services.sms.model.SMSApiMessage;
import services.sms.model.SMSApiPhone;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class TwilioSMSApiService implements ISMSApiService {

    final Logger logger = LoggerFactory.getLogger("access");

    private static String BASE_URL = "https://api.twilio.com/2010-04-01";

    private static String ACCOUNT_SID = "xxx";
    private static String AUTH_TOKEN = "xxx";

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private CampaignDAO campaignDAO;
//    private UserDAO userDAO;
//    private SettingsDAO settingsDAO;

    @Inject
    public TwilioSMSApiService(CampaignDAO campaignDAO) {
        this.campaignDAO = campaignDAO;
//        this.userDAO = userDAO;
//        this.settingsDAO = settingsDAO;
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }


    @Override
    public List<String> buyPhones(List<String> phones) {
        List<String> boughtPhones = new LinkedList();
        try {

            logger.info("Sending #buyPhones request for phones: " + phones);

            for (String phone : phones) {
                IncomingPhoneNumber incomingPhoneNumber = IncomingPhoneNumber.creator(
                                new com.twilio.type.PhoneNumber(phone))
                        .setSmsUrl(URI.create("https://bizownercells.com/api/inbound"))
                        .setVoiceUrl(URI.create("https://bizownercells.com/api/forward/twilio"))
                        .setStatusCallback(URI.create("https://bizownercells.com/api/outbond/status"))
                        .create();
                boughtPhones.add(incomingPhoneNumber.getPhoneNumber().encode(StandardCharsets.UTF_8.toString()));
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

        logger.info("Sending #searchPhones request for areaCodes: " + areaCodes);

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

                    ResourceSet<Local> local = Local.reader("US")
                            .setAreaCode(Integer.parseInt(areaCode)).limit(areacodePerPage).read();
                    //                          .setAreaCode(Integer.parseInt("800")).limit(areacodePerPage).read();

                    for (Local record : local) {
                        logger.info("Getting #searchPhones response " + record.getFriendlyName());
                        String currentPhone = record.getPhoneNumber().getEndpoint();

                        if (!strCurrentPhones.contains(currentPhone.substring(1)) && record.getCapabilities().getSms()) {
                            SMSApiPhone phone = new SMSApiPhone();
                            phone.setPhoneNumber(currentPhone);
                            phone.setRegion(record.getRegion());
                            results.add(phone);
                            strCurrentPhones.add(currentPhone.substring(1));
                        }
                    }
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

            ResourceSet<IncomingPhoneNumber> incomingPhoneNumbers =
                    IncomingPhoneNumber.reader()
                            .setPhoneNumber(new com.twilio.type.PhoneNumber(formatPhone(phone)))
                            .limit(1)
                            .read();

            for (IncomingPhoneNumber record : incomingPhoneNumbers) {
                IncomingPhoneNumber.deleter(record.getSid()).delete();
                logger.info(record.getSid());
                released = true;
            }

            logger.info("Getting #releasePhone response released: " + released);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return released;
    }

    @Override
    public String sendSMS(Long phoneTo, Long phoneFrom, String message, long userId) {
        try {
            Message response = sendSMS(phoneTo, phoneFrom, message, false);
            if (response.getSid() != null && userId != -1) {
//                scheduleRequestingSMSDetails(response.getMessageSid(), userId, response.getSentDate());
                return response.getErrorMessage();
            }
            return response.getErrorMessage();
        } catch (Exception e) {
            //TODO DROP IN PRODUCTION
            return e.getMessage();
        }
//        if (response.getSid() != null && userId != -1) {
//            scheduleRequestingSMSDetails(response.getMessageSid(), userId, response.getSentDate());
//        }
    }


    /*
        @Override
        public String sendScheduleSms(Long phoneTo, Long phoneFrom, String message, long userId,int seconds) {
            try {
                Message response = sendScheduleSms(phoneTo, phoneFrom, message, false,seconds);
                if (response.getSid() != null && userId != -1) {
                    return response.getErrorMessage();
                }
                return response.getErrorMessage();
            } catch (Exception e) {
                //TODO DROP IN PRODUCTION
                return e.getMessage();
            }
        }


     */
    @Override
    public void requestInboundMessages(String userId) {
        logger.info("Sending #requestInboundMessages request");
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

    @Override
    public void outboundMessageStatus(Map<String, String[]> data) {
        String messageSid = data.get("MessageSid")[0].replaceAll("\u0000", "");
        String messageStatus = data.get("messageStatus")[0].replaceAll("\u0000", "");

        logger.info("Getting Twilio #outboundMessageStatus SID: " + messageSid + ", Status:" + messageStatus);
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

    @Override
    public String getCarrierInfo(Long phone) {
        String carrier = "";
        logger.info("Sending #getCarrierInfo request for phone: " + phone);
        try {
            PhoneNumber phoneNumber = PhoneNumber.fetcher(
                            new com.twilio.type.PhoneNumber(formatPhone(phone)))
                    .setType(Collections.singletonList("caller-name")).fetch();
            carrier = (String) phoneNumber.getCallerName().get("caller_name");

            logger.info("Response #getCarrierInfo for number: " + phone + " Carrier: " + carrier);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error #getCarrierInfo for number " + phone + " Message: " + e.getMessage());
            logger.info("Error #getCarrierInfo for number " + phone + " Message: " + e.getMessage());
        }
        return carrier;
    }


    private Message repeatIfNeeded(boolean repeated, Long phoneTo, Long phoneFrom, String message) {
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
    private Call repeatCallIfNeeded(boolean repeated, Long phoneTo, Long phoneFrom, String message) throws URISyntaxException {
        if (!repeated) {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sendVoice(phoneTo, phoneFrom, message, true);
        }
        return null;
    }

    private Message sendScheduleSms(Long phoneTo, Long phoneFrom, String message, boolean repeated,int seconds) {
        Message messageResponse;
        logger.info("Sending #sendSMS request from: " + phoneFrom + " to: " + phoneTo + " message: " + message);
        try {
            // schedule message to be sent 61 minutes after current time
            Date sendWhen = new Date(new Date().getTime() + 61 * 60000);
            messageResponse = Message.creator(
                            new com.twilio.type.PhoneNumber(formatPhone(phoneTo)),
                            new com.twilio.type.PhoneNumber(formatPhone(phoneFrom)),
                            message).setSendAt(
                            ZonedDateTime.ofInstant(sendWhen.toInstant(), ZoneId.of("UTC")))
                    .setScheduleType(Message.ScheduleType.FIXED)
                    .setStatusCallback(URI.create("https://bizownercells.com/api/outbond/status"))
                    .create();
            logger.info("Message sent: " + messageResponse.getSid());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error #sendSMS phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            logger.info("Error #sendSMS phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            if (!repeated)
                return repeatIfNeeded(repeated, phoneTo, phoneFrom, message);
            throw e;
        }
        return messageResponse;
    }
    private Call sendVoice(Long phoneTo, Long phoneFrom, String message, boolean repeated) throws URISyntaxException {
        Call messageResponse;
        logger.info("Sending #sendVoice request from: " + phoneFrom + " to: " + phoneTo + " message: " + message);
        try {
            List<String> ls = new ArrayList<>();
            ls.add("queued");
            ls.add("initiated");
            ls.add("ringing");
            ls.add("in-progress");
            ls.add("completed");
            messageResponse = Call.creator(new com.twilio.type.PhoneNumber(formatPhone(phoneTo)),new com.twilio.type.PhoneNumber(formatPhone(phoneFrom)),
                    new URI("http://demo.twilio.com/docs/voice.xml"))
                    .setStatusCallback(URI.create("https://sand-parrot-5099.twil.io/status-callback"))
                    .setStatusCallbackMethod(HttpMethod.POST)
                    .setStatusCallbackEvent(ls)
                    .create();

            logger.info("Message sent: " + messageResponse.getSid());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error #sendVoice phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            logger.info("Error #sendVoice phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            if (!repeated)
                return repeatCallIfNeeded(repeated, phoneTo, phoneFrom, message);
            throw e;
        }
        return messageResponse;
    }

    private Message sendSMS(Long phoneTo, Long phoneFrom, String message, boolean repeated) {
        Message messageResponse;
        logger.info("Sending #sendSMS request from: " + phoneFrom + " to: " + phoneTo + " message: " + message);
        try {
            messageResponse = Message.creator(
                            new com.twilio.type.PhoneNumber(formatPhone(phoneTo)),
                            new com.twilio.type.PhoneNumber(formatPhone(phoneFrom)),
                            message)
                    .setStatusCallback(URI.create("http://dev.wsdevworld.com:9000/api/outbond/status"))
                    .create();
            logger.info("Message sent: " + messageResponse.getSid());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error #sendSMS phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            logger.info("Error #sendSMS phoneTo " + phoneTo + " phoneFrom: " + phoneFrom + " #Error: " + e.getMessage());
            if (!repeated)
                return repeatIfNeeded(repeated, phoneTo, phoneFrom, message);
            throw e;
        }
        return messageResponse;
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

    private void requestInboundMessage(Chat chat) {
        try {
            logger.info("Sending #requestInboundMessage request for phone from: " + chat.getPhoneFrom());

            ResourceSet<Message> messages = Message.reader()
                    .setDateSent(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC")))
                    .setTo(new com.twilio.type.PhoneNumber(formatPhone(chat.getPhoneTo())))
//                    .limit(20)
                    .read();

            for (Message record : messages) {
                if (record.getStatus() == Message.Status.SENT || record.getStatus() == Message.Status.RECEIVED) {
                    SMSApiMessage messageChat = new SMSApiMessage();
                    messageChat.setMessageSid(record.getSid());
                    messageChat.setBody(record.getBody());
//                    messageChat.setDateSent(dateFormat.format(record.getDateSent()));
                    messageChat.setDateSent(String.valueOf(record.getDateSent()));

                    campaignDAO.updateExternalChatMessage(chat, messageChat);
                }
                campaignDAO.updateHasInboundForChat(chat.getId(), chat.getUserId(), true);

                logger.info("Getting #requestInboundMessage response: getSid " + record.getSid());
                logger.info("Getting #requestInboundMessage response: getStatus " + record.getStatus());
                logger.info("Getting #requestInboundMessage response: getDateSent " + record.getDateSent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
