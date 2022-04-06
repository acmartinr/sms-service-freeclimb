package controllers;

import com.google.inject.Inject;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import common.Utils;
import model.CommonResponse;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.Phone;
import services.database.model.Setting;
import services.database.model.User;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SystemController extends Controller {

    private SettingsDAO settingsDAO;
    private CampaignDAO campaignDAO;
    private UserDAO userDAO;

    @Inject
    public SystemController(SettingsDAO settingsDAO,
                            CampaignDAO campaignDAO,
                            UserDAO userDAO) {
        this.settingsDAO = settingsDAO;
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
    }

    @With(LoggingAction.class)
    public Result currentDate() {
        return ok(Json.toJson(System.currentTimeMillis()));
    }

    @With(LoggingAction.class)
    public Result configuringSettings(Long userId, String type) {
        List<Setting> settings = new LinkedList();

        List<String> userSettings = new LinkedList();
        if ("master_ignore".equalsIgnoreCase(type)) {
            userSettings.add("master.ignore_" + userId);
        } else if ("chat_carrier_lookup".equalsIgnoreCase(type)) {
            userSettings.add("chat.carrier.lookup_" + userId);
        } else {
            userSettings.add("carrier.ignore.verizon_" + userId);
            userSettings.add("carrier.ignore.att_" + userId);
            userSettings.add("carrier.ignore.t-mobile_" + userId);
            userSettings.add("carrier.ignore.sprint_" + userId);
        }

        User user = userDAO.getUserById(userId);

        for (String userSetting : userSettings) {
            Setting setting = settingsDAO.getSettingByKey(userSetting, user.getId());
            if (setting == null) {
                setting = new Setting(
                        userSetting,
                        settingsDAO.getSettingByKey(
                                userSetting.split("_")[0],
                                user.getResellerId()).getSval(),
                        userId);
            }
            settings.add(setting);
        }

        return ok(Json.toJson(CommonResponse.OK(settings)));
    }

    @With(LoggingAction.class)
    public Result settings(Long userId) {
        List<Setting> settings = settingsDAO.getAllSettings(userId);
        Iterator<Setting> it = settings.iterator();
        while (it.hasNext()) {
            Setting setting = it.next();

            if (setting.getSkey().contains("_") ||
                    setting.getSkey().contains("auto.reply.enabled") ||
                    setting.getSkey().contains("master.ignore.enabled") ||
                    setting.getSkey().contains("consumerdnc.upload.filter.ignore") ||
                    setting.getSkey().contains("carrier.ignore.enabled") ||
                    setting.getSkey().contains("chat.carrier.lookup.enabled") ||
                    setting.getSkey().contains("chat.carrier.lookup") ||
                    setting.getSkey().contains("phones.bulk.forward.enabled") ||
                    setting.getSkey().contains("download.lists.enabled") ||
                    setting.getSkey().contains("carrier.ignore.verizon") ||
                    setting.getSkey().contains("carrier.ignore.att") ||
                    setting.getSkey().contains("carrier.ignore.sprint") ||
                    setting.getSkey().contains("carrier.ignore.t-mobile") ||
                    setting.getSkey().contains("master.ignore") ||
                    setting.getSkey().contains("agents.login.enabled")) {
                it.remove();
            }
        }

        /*if (settings.size() == 0) {
            settingsDAO.insertSetting(new Setting("phone.forwarding", "", userId));
            settings = settingsDAO.getAllSettings(userId);
        }*/

        return ok(Json.toJson(CommonResponse.OK(settings)));
    }

    @With(LoggingAction.class)
    public Result updateSetting(Http.Request request) {
        Setting setting = Json.fromJson(request.body().asJson(), Setting.class);

        if (setting.getId() > 0) {
            settingsDAO.updateSetting(setting);
        } else if ("Ignore blockers".equalsIgnoreCase(setting.getSkey()) ||
                "Ignore Verizon only".equalsIgnoreCase(setting.getSkey())) {
            List<Setting> userSettings = new LinkedList();

            if ("Ignore blockers".equalsIgnoreCase(setting.getSkey())) {
                userSettings.add(
                        new Setting("carrier.ignore.verizon_" + setting.getUserId(), setting.getSval()));
                userSettings.add(
                        new Setting("carrier.ignore.att_" + setting.getUserId(), setting.getSval()));
                userSettings.add(
                        new Setting("carrier.ignore.t-mobile_" + setting.getUserId(), setting.getSval()));
                userSettings.add(
                        new Setting("carrier.ignore.sprint_" + setting.getUserId(), setting.getSval()));
            } else if ("Ignore Verizon only".equalsIgnoreCase(setting.getSkey())) {
                userSettings.add(
                        new Setting("carrier.ignore.verizon_" + setting.getUserId(), setting.getSval()));
                userSettings.add(
                        new Setting("carrier.ignore.att_" + setting.getUserId(), "0"));
                userSettings.add(
                        new Setting("carrier.ignore.t-mobile_" + setting.getUserId(), "0"));
                userSettings.add(
                        new Setting("carrier.ignore.sprint_" + setting.getUserId(), "0"));
            }

            for (Setting userSetting : userSettings) {
                Setting dbSetting = settingsDAO.getSettingByKey(userSetting.getSkey(), setting.getUserId());
                if (dbSetting != null) {
                    dbSetting.setSval(userSetting.getSval());
                    settingsDAO.updateSetting(dbSetting);
                } else {
                    dbSetting = new Setting(
                            userSetting.getSkey(),
                            userSetting.getSval(),
                            setting.getUserId());
                    settingsDAO.insertSetting(dbSetting);
                }
            }

        } else {
            settingsDAO.insertSetting(setting);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result phoneForward(Http.Request request) throws Exception {
        Map<String, String[]> data = request.body().asFormUrlEncoded();
        String strPhoneTo = data.get("To")[0];

        if (strPhoneTo != null) {
            long phoneTo = Long.parseLong(strPhoneTo.replace("+", "").trim());
            Phone phone = campaignDAO.getPhoneByNumber(phoneTo);

            if (phone != null) {
                Long forwardingPhone = phone.getForwarding();
                if (forwardingPhone != null && forwardingPhone != 0) {
                    String response = "<response><dial>+{phone}</dial></response>".replace("{phone}",
                            Utils.formatPhone(forwardingPhone).toString());

                    File file = File.createTempFile("response", ".xml");

                    PrintWriter printWriter = new PrintWriter(file);
                    printWriter.append(response);
                    printWriter.flush();

                    return ok(file).as("text/xml");
                }
            }
        }

        return notFound();
    }

    @With(LoggingAction.class)
    public Result phoneForwardTwilio(Http.Request request) {

        try {
            Map<String, String[]> data = request.body().asFormUrlEncoded();
            String strPhoneTo = data.get("To")[0];

            System.out.println("#phoneForwardTwilio call to: " + strPhoneTo);

            if (strPhoneTo != null) {
                long phoneTo = Long.parseLong(strPhoneTo.replace("+", "").trim());
                Phone phone = campaignDAO.getPhoneByNumber(phoneTo);

                if (phone != null) {
                    Long forwardingPhone = phone.getForwarding();

                    Dial dial = new Dial.Builder("+" + forwardingPhone).build();
//                    Say say = new Say.Builder("Goodbye").build();
                    VoiceResponse response = new VoiceResponse.Builder().dial(dial)
//                            .say(say)
                            .build();
                    return ok(response.toXml()).as("application/xml");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return notFound();
    }

}
