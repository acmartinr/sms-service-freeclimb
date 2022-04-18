package services.sms;

import services.database.model.Chat;
import services.sms.model.SMSApiPhone;

import java.util.List;
import java.util.Map;

public interface ISMSApiService {

    List<String> buyPhones(List<String> phones);

    List<SMSApiPhone> searchPhones(List<String> areaCodes, List<String> strCurrentPhone);

    boolean releasePhone(Long phone);


    String sendSMS(Long phoneTo, Long phoneFrom, String message, long userId);

    void requestInboundMessages(String userId);

    String getCarrierInfo(Long phone);

    void outboundMessageStatus(Map<String, String[]> data);

    Chat parseInboundMessage(Map<String, String[]> data);

}


