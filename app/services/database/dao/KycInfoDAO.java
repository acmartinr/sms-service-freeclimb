package services.database.dao;

import com.google.inject.Inject;
import model.KycInfo;
import services.database.mapper.KycInfoMapper;

public class KycInfoDAO {

    private final KycInfoMapper mapper;

    @Inject
    public KycInfoDAO(KycInfoMapper mapper) {
        this.mapper = mapper;
    }

    public void updateKycInfo(KycInfo kycInfo) {
        mapper.updateKycInfo(kycInfo);
    }

    public void insertKycInfo(KycInfo kycInfo) {
        mapper.insertKycInfo(kycInfo);
    }

    public KycInfo getKycInfoByUserId(long userId) {
        KycInfo kycInfo = mapper.findKycInfoByUserId(userId);
//        if (kycInfo == null) {
//            kycInfo = mapper.getSettingByKeyAndUserId(skey, 1);
//        }

        return kycInfo;
    }
}
