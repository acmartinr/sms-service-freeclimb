package tasks;

import akka.actor.AbstractActor;
import com.google.inject.Inject;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.Phone;
import services.database.model.Transaction;
import services.database.model.User;
import services.sms.ISMSApiService;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

class CallerIDPaymentActor extends AbstractActor {

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;
    private ISMSApiService smsApiService;

    @Inject
    public CallerIDPaymentActor(CampaignDAO campaignDAO,
                                UserDAO userDAO,
                                SettingsDAO settingsDAO,
                                ISMSApiService smsApiService) {
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
        this.smsApiService = smsApiService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(o -> checkCallerIDPayments()).build();
    }

    private void checkCallerIDPayments() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        long monthAgoDate = calendar.getTimeInMillis();

        List<Phone> phones =  campaignDAO.getAllPhones();
        for (Phone phone: phones) {
            long date = phone.getDate();
            if (phone.getChargedDate() != 0) {
                date = phone.getChargedDate();
            }

            if (date < monthAgoDate - 60 * 60 * 1000) {
                renewPhone(phone, date, monthAgoDate);
            }
        }
    }

    private void renewPhone(Phone phone, long date, long monthAgoDate) {
        User user = userDAO.getUserById(phone.getUserId());
        Float phonePrice = settingsDAO.getPhonePrice(phone.getUserId(), user.getResellerId());
        Float basePhonePrice = settingsDAO.getPhonePrice(user.getResellerId(), User.DEFAULT_RESELLER_ID);

        if (phonePrice != null && basePhonePrice != null) {
            float amount = -phonePrice;
            float baseAmount = -basePhonePrice;

            boolean userCondition = user != null && user.getBalance() < phonePrice;
            boolean usingCondition = phone.getLastSentDate() < monthAgoDate && phone.getPhone() != 14104579779l;

            if (userCondition || usingCondition) {
                System.out.println("Trying to remove caller id: " + phone.getPhone());
                removeCallerId(phone);
            } else {
                System.out.println("Trying to renew caller id: " + phone.getPhone());
                renewCallerId(phone, amount, baseAmount, date);
            }
        }
    }

    private void removeCallerId(Phone phone) {
        boolean result = smsApiService.releasePhone(phone.getPhone());
        if (result) {
            campaignDAO.removePhoneById(phone.getId());
            campaignDAO.removeSendersByPhoneAndUserId(phone.getPhone(), phone.getUserId());
        }
    }

    private void renewCallerId(Phone phone, float amount, float baseAmount, long date) {
        /*Transaction transaction = new Transaction(
                phone.getUserId(), amount, Transaction.PHONE_RENEW,
                System.currentTimeMillis(), "+" + phone.getPhone());*/

        userDAO.updateCountTransaction(Transaction.PHONE_RENEW, phone.getUserId(), amount, baseAmount, "phones count: ");
        userDAO.changeUserBalanceAndUpdateResellerBalance(Transaction.PHONE_RENEW, phone.getUserId(), amount, baseAmount);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.MONTH, 1);

        campaignDAO.updatePhoneChargedDateById(phone.getId(), calendar.getTimeInMillis());
    }
}