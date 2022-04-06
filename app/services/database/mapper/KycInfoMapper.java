package services.database.mapper;

import model.KycInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface KycInfoMapper {

    @Select("SELECT * FROM kycinfo WHERE userId=#{userId} limit 1")
    KycInfo findKycInfoByUserId(@Param("userId") long userId);

    @Insert("<script>INSERT INTO kycinfo(userId <if test='businessTypeId != 0'> ,companyName, brandName, companyCountry, companyTaxId, businessTypeId, companyWebsite, companyStockExchange, " +
            "companyAddressStreet, companyAddressZip, companyAddressCity, companyAddressState, companyAddressCountry, contactFirstName, contactLastName, " +
            "contactPhone, contactEmail, billingEmail, contactTollFreePhone, supportEmail, lastUpdate, businessIndustryId, businessTitle, businessPosition, confirmedTerms, " +
            "confirmedProviderContact</if>" +
            "<if test='useCaseId != 0'>,useCaseId,relevantAttributesIds,campaignDescription,messageSample</if>, completed) VALUES(#{userId}" +
            "<if test='businessTypeId != 0'>,#{companyName},#{brandName},#{companyCountry},#{companyTaxId},#{businessTypeId},#{companyWebsite},#{companyStockExchange}," +
            "#{companyAddressStreet},#{companyAddressZip},#{companyAddressCity},#{companyAddressState},#{companyAddressCountry},#{contactFirstName}," +
            "#{contactLastName},#{contactPhone},#{contactEmail},#{billingEmail},#{contactTollFreePhone},#{supportEmail},#{lastUpdate},#{businessIndustryId}," +
            "#{businessTitle},#{businessPosition},#{confirmedTerms},#{confirmedProviderContact}</if> " +
            "<if test='useCaseId != 0'> ,#{useCaseId},#{relevantAttributesIds},#{campaignDescription},#{messageSample}</if>, #{completed})</script>")
    void insertKycInfo(KycInfo kycInfo);

    @Update("UPDATE kycinfo SET userId=#{userId}, companyName=#{companyName}, brandName=#{brandName}, companyCountry=#{companyCountry}, " +
            "companyTaxId=#{companyTaxId}, businessTypeId=#{businessTypeId}, companyWebsite=#{companyWebsite}, companyStockExchange=#{companyStockExchange}," +
            " companyAddressStreet=#{companyAddressStreet}, companyAddressZip=#{companyAddressZip}, companyAddressCity=#{companyAddressCity}, " +
            "companyAddressState=#{companyAddressState}, companyAddressCountry=#{companyAddressCountry}, contactFirstName=#{contactFirstName}, " +
            "contactLastName=#{contactLastName}, contactPhone=#{contactPhone}, contactEmail=#{contactEmail}, billingEmail=#{billingEmail}, " +
            "contactTollFreePhone=#{contactTollFreePhone}, supportEmail=#{supportEmail}, lastUpdate=#{lastUpdate}, businessIndustryId=#{businessIndustryId}, " +
            "businessTitle=#{businessTitle}, businessPosition=#{businessPosition}, confirmedTerms=#{confirmedTerms}, confirmedProviderContact=#{confirmedProviderContact}, " +
            "useCaseId=#{useCaseId},relevantAttributesIds=#{relevantAttributesIds},campaignDescription=#{campaignDescription},messageSample=#{messageSample},completed=#{completed} WHERE id=#{id}")
    void updateKycInfo(KycInfo kycInfo);

}
