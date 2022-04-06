package services.database.dao;

import com.google.inject.Inject;
import services.database.mapper.SettingsMapper;
import services.database.model.Setting;
import services.database.model.User;

import java.util.LinkedList;
import java.util.List;

public class SettingsDAO {

    private SettingsMapper mapper;

    @Inject
    public SettingsDAO(SettingsMapper mapper) {
        this.mapper = mapper;
    }

    public Long getSystemPhone(long resellerId) {
        String value = mapper.getValueByKeyAndUserId("phone.system", resellerId);

        if (value != null) {
            return Long.parseLong(value.replace("+", ""));
        }

        return null;
    }

    public List<Setting> getAllSettings(long userId) {
        return mapper.getAllSettings(userId);
    }

    public void updateSetting(Setting setting) {
        mapper.updateSetting(setting);
    }

    public Integer getPhonesLimit(long resellerId) {
        String value = mapper.getValueByKeyAndUserId("phone.limit", resellerId);

        if (value != null) {
            return Integer.parseInt(value);
        }

        return null;
    }

    public Float getPhonePrice(long userId, long resellerId) {
        String value = mapper.getValueByKeyAndUserId("price.phone_" + userId, resellerId);
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("price.phone", resellerId);
        }

        if (value != null) {
            return Float.parseFloat(value);
        }

        return null;
    }

    public Float getInboundMessagePrice(long userId, long resellerId) {
        String value = mapper.getValueByKeyAndUserId("price.sms.inbound_" + userId, resellerId);
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("price.sms.inbound", resellerId);
        }

        if (value != null) {
            return Float.parseFloat(value);
        }

        return null;
    }

    public Float getOutboundMessagePrice(long userId, long resellerId) {
        String value = mapper.getValueByKeyAndUserId("price.sms.outbound_" + userId, resellerId);
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("price.sms.outbound", resellerId);
        }

        /*!!!ADD ME TO ALL METHODS
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("price.sms.outbound", User.DEFAULT_RESELLER_ID);
        }
        !!!ADD ME TO ALL METHODS*/

        if (value != null) {
            return Float.parseFloat(value);
        }

        return null;
    }

    public void insertSetting(Setting setting) {
        mapper.insertSetting(setting);
    }

    public Setting getSettingByKey(String skey, long resellerId) {
        Setting setting = mapper.getSettingByKeyAndUserId(skey, resellerId);
        if (setting == null) {
            setting = mapper.getSettingByKeyAndUserId(skey, 1);
        }

        return setting;
    }

    public Setting getSettingOrDefaultByKey(String key, long resellerId) {
        Setting setting = getSettingByKey(key, resellerId);
        if (setting == null) {
            setting = new Setting(
                    key,
                    getSettingByKey(key.split("_")[0], resellerId).getSval());
        }

        return setting;
    }

    public void updateResellerSettings(long id) {
        List<Setting> settings = getAllSettings(id);
        if (settings.size() == 0) {
            insertSetting(new Setting("phone.limit", "50", id));
            insertSetting(new Setting("price.phone", "1", id));
            insertSetting(new Setting("price.sms.inbound", "0.02", id));
            insertSetting(new Setting("price.sms.outbound", "0.015", id));
            insertSetting(new Setting("price.lookup", "0.0015", id));
            insertSetting(new Setting("surcharge.factor", "1.0", id));
        }
    }

    public Float getLookupPrice(long userId, long resellerId) {
        String value = mapper.getValueByKeyAndUserId("price.lookup_" + userId, resellerId);
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("price.lookup", resellerId);
        }

        if (value != null) {
            return Float.parseFloat(value);
        }

        return 0.0015f;
    }

    public List<String> getDNCWords() {
        String value = mapper.getValueByKeyAndUserId("dnc.words", User.DEFAULT_RESELLER_ID);
        if (value != null) {
            String[] words = value.trim().split(",");

            List<String> result = new LinkedList();
            for (String word : words) {
                result.add(word.trim());
            }

            return result;
        } else {
            return new LinkedList();
        }
    }

    public Float getSurchargeFactor(long userId, long resellerId) {
        String value = mapper.getValueByKeyAndUserId("surcharge.factor_" + userId, resellerId);
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("surcharge.factor", resellerId);
        }
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("surcharge.factor", User.DEFAULT_RESELLER_ID);
        }
        if (value != null) {
            return Float.parseFloat(value);
        }
        return Float.parseFloat("1");
    }

    public boolean isConsumerDNCUploadFilterIgnore(long userId, long resellerId) {
        String value = mapper.getValueByKeyAndUserId("consumerdnc.upload.filter.ignore_" + userId, resellerId);
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("consumerdnc.upload.filter.ignore", resellerId);
        }
        if (value == null) {
            value = mapper.getValueByKeyAndUserId("consumerdnc.upload.filter.ignore", User.DEFAULT_RESELLER_ID);
        }
        if (value != null) {
            return value.equals("1");
        }
        return false;
    }
}
