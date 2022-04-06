package common;

import play.mvc.Http;
import services.database.dao.CampaignDAO;
import services.database.dao.UserDAO;
import services.database.model.Campaign;
import services.database.model.User;

import java.util.*;

public class Utils {

    public static final Map<String, String> SESSIONS = new HashMap();
    private static final int[] areaCodes = {210, 979, 975, 845, 332, 877, 862, 579, 551, 909, 915, 956, 604, 947, 881, 325, 880, 627, 641, 223, 268, 855, 564, 346, 919, 623, 240, 951, 567, 908, 442, 315, 519, 807, 819, 929, 869, 706, 701, 203, 818, 581, 574, 628, 508, 935, 600, 269, 264, 309, 931, 423, 718, 402, 927, 278, 518, 976, 713, 772, 808, 805, 630, 435, 878, 843, 419, 510, 913, 281, 747, 669, 949, 418, 661, 562, 254, 770, 709, 778, 832, 664, 959, 500, 306, 764, 811, 866, 507, 865, 615, 365, 787, 441, 856, 734, 585, 557, 224, 708, 573, 341, 209, 207, 660, 206, 850, 416, 409, 815, 464, 570, 352, 740, 602, 847, 800, 248, 639, 502, 301, 714, 828, 809, 334, 404, 408, 649, 231, 631, 720, 320, 305, 757, 430, 411, 580, 424, 916, 312, 313, 214, 888, 211, 504, 480, 267, 217, 868, 559, 712, 618, 952, 612, 863, 611, 470, 517, 984, 763, 256, 882, 260, 782, 629, 270, 829, 765, 509, 514, 239, 603, 814, 725, 710, 817, 822, 774, 662, 360, 405, 539, 900, 849, 620, 940, 438, 607, 413, 810, 903, 831, 773, 616, 700, 758, 318, 609, 626, 530};


    public static Long formatPhone(Long phone) {
        if (phone == null) {
            return null;
        }

        String strPhone = phone.toString();
        if (!strPhone.startsWith("1")) {
            return Long.parseLong("1" + strPhone);
        }

        return phone;
    }

    public static User getCurrentUser(Http.Request request, UserDAO userDAO) {
        Optional<String> tokenHeader = request.getHeaders().get("token");
        if (tokenHeader.isPresent()) {
            String username = SESSIONS.get(tokenHeader.get());
            if (username != null) {
                return userDAO.findUserByUsernameOrEmail(username);
            }
        }

        return null;
    }

    public static Long getCurrentUserId(Http.Request request, UserDAO userDAO, CampaignDAO campaignDAO) {
        User currentUser = Utils.getCurrentUser(request, userDAO);
        Long userId = null;
        if (currentUser != null) {
            userId = currentUser.getId();

            if (currentUser.getRole() == User.LIMITED) {
                Campaign campaign = campaignDAO.getCampaignByAgentUsername(currentUser.getUsername());
                if (campaign != null) {
                    userId = campaign.getUserId();
                }
            }
        }

        return userId;
    }

    public static void registerCurrentUser(String sessionId, User user) {
        /*if (SESSIONS.containsValue(user.getUsername())) {
            Iterator<Map.Entry<String, String>> it = SESSIONS.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next().getValue().equals(user.getUsername())) {
                    it.remove();
                    break;
                }
            }
        }*/

        SESSIONS.put(sessionId, user.getUsername());
    }

    public static String getRandomUsaAreaCode() {

        int rnd = new Random().nextInt(areaCodes.length);
        return Integer.toString(areaCodes[rnd]);
    }

    public static int getUsaAreaCodeSize() {
        return areaCodes.length;
    }
}
