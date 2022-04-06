package controllers;

import com.google.inject.Inject;
import common.Utils;
import model.AddDNCPhonesRequest;
import model.CampaignListsResponse;
import model.CommonRequest;
import model.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import services.database.dao.CampaignDAO;
import services.database.dao.SettingsDAO;
import services.database.dao.UserDAO;
import services.database.model.*;

import java.io.*;
import java.util.*;

public class ListsController extends Controller {
    final Logger logger = LoggerFactory.getLogger("access");

    public static final int CODE_LENGTH = 5;

    private CampaignDAO campaignDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    @Inject
    public ListsController(CampaignDAO campaignDAO,
                           UserDAO userDAO, SettingsDAO settingsDAO) {
        this.campaignDAO = campaignDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
    }

    @With(LoggingAction.class)
    public Result lists(Http.Request request) {
        CommonRequest commonRequest = Json.fromJson(request.body().asJson(), CommonRequest.class);

        return ok(Json.toJson(CommonResponse.OK(new CampaignListsResponse(
                campaignDAO.getListsByRequest(commonRequest),
                campaignDAO.getListsCountByRequest(commonRequest)
        ))));
    }

    @With(LoggingAction.class)
    public Result removeList(Long listId) {
        campaignDAO.removeListById(listId);
        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result downloadListFile(Long listId) throws Exception {
        File listFile = File.createTempFile("temp", "csv");
        CampaignList list = campaignDAO.getListById(listId);
        if (list != null) {
            campaignDAO.copyPhonesToFiles(listId, new FileWriter(listFile));

            return ok(listFile).withHeader("Content-disposition", "attachment; filename=" +
                    list.getName()).as("text/csv");
        } else {
            return notFound();
        }
    }

    @With(LoggingAction.class)
    public Result uploadListFile(Long userId, Http.Request request) {
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file = body.getFile("file");

        if (file != null) {
            CampaignList list = new CampaignList(userId, file.getFilename(), System.currentTimeMillis(), -1, 0);
            campaignDAO.insertList(list);
            handleUploadedListFileBackground(list, file, userId);

            return ok(Json.toJson(CommonResponse.OK(file.getFilename())));
        } else {
            return ok(Json.toJson(CommonResponse.ERROR()));
        }
    }

    private void handleUploadedListFileBackground(CampaignList list, Http.MultipartFormData.FilePart<File> file, Long userId) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file.getFile()));
                String line = reader.readLine();

                Map<Integer, Set<Long>> phonesMap = new HashMap();
                Map<Integer, Set<Long>> dncPhonesMap = settingsDAO.isConsumerDNCUploadFilterIgnore(userId, User.DEFAULT_RESELLER_ID) ? new HashMap() :
                        campaignDAO.generateMasterDNCPhonesMap();

                long count = 0;
                while (line != null) {
                    try {
                        List<String> data = extractDataFromLine(line);
                        line = removeNonDigitalSymbols(line);

                        if (line.length() > CODE_LENGTH) {
                            Long phone = Long.parseLong(line);
                            phone = Utils.formatPhone(phone);

                            int code = Integer.parseInt(phone.toString().substring(0, CODE_LENGTH));

                            if (!phonesMap.containsKey(code)) {
                                phonesMap.put(code, new HashSet());
                            }

                            if (dncPhonesMap.isEmpty() || dncPhonesMap.get(code) == null || !dncPhonesMap.get(code).contains(phone)) {
                                if (!phonesMap.get(code).contains(phone)) {
                                    campaignDAO.insertListPhone(list.getId(), phone, data);
                                    count = count + 1;

                                    phonesMap.get(code).add(phone);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    line = reader.readLine();
                }

                campaignDAO.updateListCountById(list.getId(), count);
            } catch (Exception e) {
                campaignDAO.updateListCountById(list.getId(), -2);
                e.printStackTrace();
            }
        }).start();
    }

    private String removeNonDigitalSymbols(String line) {
        line = line
                .replaceAll("\"", "")
                .replaceAll(" ", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\+", "")
                .replaceAll("-", "")
                .replaceAll(":", "");

        if (!line.equals(",")) {
            return line.split(",")[0];
        } else {
            return line;
        }
    }

    private List<String> extractDataFromLine(String line) {
        List<String> results = new LinkedList();
        line = line.replaceAll("\"", "");

        String[] parts = line.split(",");
        if (parts.length > 1) {
            results.add(parts[1]);
        }

        if (parts.length > 2) {
            results.add(parts[2]);
        }

        return results;
    }

    @With(LoggingAction.class)
    public Result DNCLists(Http.Request request) {
        List<DNCList> lists = campaignDAO.getDNCLists();

        Iterator<DNCList> it = lists.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equalsIgnoreCase(DNCList.MASTER)) {
                it.remove();
            }
        }

        return ok(Json.toJson(CommonResponse.OK(lists)));
    }

    @With(LoggingAction.class)
    public Result uploadMasterDNCListFile(Http.Request request) {
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file = body.getFile("file");

        if (file != null) {
            DNCList dncList = campaignDAO.getDNCListByName(DNCList.MASTER);
            if (dncList != null) {
                campaignDAO.updateDNCListCountAndDateById(dncList.getId(), -1);
                handleDNCFileBackground(dncList, file);
            }

            return ok(Json.toJson(CommonResponse.OK(file.getFilename())));
        } else {
            return ok(Json.toJson(CommonResponse.ERROR()));
        }
    }

    @With(LoggingAction.class)
    public Result uploadDNCListFile(Long userId, Http.Request request) {
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> file = body.getFile("file");

        if (file != null) {
            DNCList dncList = campaignDAO.getDNCListByName(DNCList.MAIN);
            if (dncList != null) {
                if (file.getFileSize() > 3 * 1024 * 1024) {
                    CommonResponse response = CommonResponse.OK(file.getFilename());
                    response.setData("size.error");

                    return ok(Json.toJson(response));
                }

                campaignDAO.updateDNCListCountAndDateById(dncList.getId(), -1);
                handleDNCFileBackground(dncList, file);
            }

            return ok(Json.toJson(CommonResponse.OK(file.getFilename())));
        } else {
            return ok(Json.toJson(CommonResponse.ERROR()));
        }
    }

    private void handleDNCFileBackground(DNCList list, Http.MultipartFormData.FilePart<File> file) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file.getFile()));
                String line = reader.readLine();

                Map<Integer, Set<Long>> phonesMap;
                if (DNCList.MASTER.equalsIgnoreCase(list.getName())) {
                    phonesMap = campaignDAO.generateMasterDNCPhonesMap();
                } else {
                    phonesMap = campaignDAO.generateDNCPhonesMap(list);
                }

                List<Long> phones = new LinkedList();

                while (line != null) {
                    try {
                        line = removeNonDigitalSymbols(line);
                        if (line.length() > CODE_LENGTH) {
                            Long phone = Long.parseLong(line);
                            phone = Utils.formatPhone(phone);

                            if (phone.toString().length() > CODE_LENGTH) {
                                int code = Integer.parseInt(phone.toString().substring(0, CODE_LENGTH));

                                if (!phonesMap.containsKey(code)) {
                                    phonesMap.put(code, new HashSet());
                                }

                                if (!phonesMap.get(code).contains(phone)) {
                                    phones.add(phone);

                                    if (phones.size() > 10000) {
                                        campaignDAO.insertDNCListPhones(list, phones);
                                        phones.clear();
                                    }

                                    phonesMap.get(code).add(phone);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    line = reader.readLine();
                }

                if (phones.size() > 10000) {
                    campaignDAO.insertDNCListPhones(list, phones);
                    phones.clear();
                }

                campaignDAO.updateDNCListCountAndDateById(list.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @With(LoggingAction.class)
    public Result addDNCPhones(Http.Request request) {
        AddDNCPhonesRequest addDNCPhonesRequest = Json.fromJson(request.body().asJson(), AddDNCPhonesRequest.class);
        DNCList dncList = campaignDAO.getDNCListById(addDNCPhonesRequest.getListId());

        campaignDAO.updateDNCListCountAndDateById(dncList.getId(), -1);

        for (Long phone : addDNCPhonesRequest.getPhones()) {
            phone = Utils.formatPhone(phone);
            Long dbPhone = campaignDAO.getDNCPhoneByListId(dncList.getId(), phone);

            if (dbPhone == null) {
                campaignDAO.insertDNCListPhone(dncList, phone);
            }
        }

        campaignDAO.updateDNCListCountAndDateById(dncList.getId());

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result banChat(Http.Request request, Long chatId) {
        Long userId = Utils.getCurrentUserId(request, userDAO, campaignDAO);

        if (userId == null) {
            return forbidden();
        }

        Chat chat = campaignDAO.getChatById(chatId, userId);
        if (chat != null) {
            campaignDAO.banChat(chat);
        } else {
            logger.error("Baning chat error ==> " + chatId + " userid:" + userId);
        }

        return ok(Json.toJson(CommonResponse.OK()));
    }

    @With(LoggingAction.class)
    public Result downloadDNCListFile(Long listId) throws Exception {
        File listFile = File.createTempFile("temp", "csv");
        DNCList list = campaignDAO.getDNCListById(listId);
        if (list != null) {
            campaignDAO.copyDNCPhonesToFiles(listId, new FileWriter(listFile));

            return ok(listFile).withHeader("Content-disposition", "attachment; filename=" +
                    list.getName()).as("text/csv");
        } else {
            return notFound();
        }
    }

}
