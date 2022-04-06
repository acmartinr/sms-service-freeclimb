package services.database.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import services.database.model.Setting;

import java.util.List;

public interface SettingsMapper {

    @Select("SELECT sval FROM settings WHERE skey=#{key} AND userId=#{userId}")
    String getValueByKeyAndUserId(@Param("key") String key,
                                  @Param("userId") long userId);

    @Select("SELECT * FROM settings WHERE userId=#{userId} ORDER BY skey")
    List<Setting> getAllSettings(@Param("userId") long userId);

    @Update("UPDATE settings SET sval=#{sval} WHERE id=#{id}")
    void updateSetting(Setting setting);

    @Insert("INSERT INTO settings(skey,sval,userId) VALUES(#{skey},#{sval},#{userId})")
    void insertSetting(Setting setting);

    @Select("SELECT * FROM settings WHERE skey=#{skey} AND userId=#{userId} LIMIT 1")
    Setting getSettingByKeyAndUserId(@Param("skey") String skey,
                                     @Param("userId") long userId);
}
