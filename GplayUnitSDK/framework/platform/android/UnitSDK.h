//
// Created by 章军飞 on 15/12/4.
//

/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
 */

#ifndef GPLAY_GPLAYUNITSDK_H
#define GPLAY_GPLAYUNITSDK_H

#include <vector>
#include <map>
#include <string>

using namespace std;
namespace gplay { namespace framework {

typedef enum {
    kTypeUser = 1,      // type of User.
    kTypeIAP = 2,       // type of IAP.
    kTypeShare = 4,     // type of Share.
    kTypeShortcut = 8   // type of Ads.
}SupportType;

typedef enum {
    GPLAY_INIT_SUCCESS = 0,                 // succeeding in initing sdk
    GPLAY_INIT_FAIL = 1,                    // failing to init sdk

    USER_LOGIN_RESULT_SUCCESS = 10000,      // login success
    USER_LOGIN_RESULT_FAIL = 10001,         // login failed
    USER_LOGIN_RESULT_CANCEL = 10002,       // login canceled
    USER_LOGOUT_RESULT_SUCCESS = 10003,     // logout success
    USER_LOGOUT_RESULT_FAIL = 10004,        // logout failed
    USER_REGISTER_RESULT_SUCCESS = 10005,   // regiister sucess
    USER_REGISTER_RESULT_FAIL = 10006,      // regiister failed
    USER_REGISTER_RESULT_CANCEL = 10007,    // regiister Cancel
    USER_BIND_RESULT_SUCESS = 10008,        // bind sucess
    USER_BIND_RESULT_CANCEL = 10009,        // bind Cancel
    USER_BIND_RESULT_FAILED = 100010,       // bind failed
    USER_RESULT_NETWROK_ERROR = 10011,      // network error
    USER_RESULT_USEREXTENSION = 19999,      // extension code

    PAY_RESULT_SUCCESS = 20000,             // pay success
    PAY_RESULT_FAIL = 20001,                // pay fail
    PAY_RESULT_CANCEL = 20002,              // pay cancel
    PAY_RESULT_INVALID = 20003,             // incompleting info
    PAY_RESULT_NETWORK_ERROR = 20004,       // network error
    PAY_RESULT_NOW_PAYING = 20005,          // paying now
    PAY_RESULT_PAYEXTENSION = 29999,        // extension code
    
    SHARE_RESULT_SUCCESS = 30000,           // share success
    SHARE_RESULT_FAIL = 30001,              // share failed
    SHARE_RESULT_CANCEL = 30002,            // share canceled
    SHARE_RESULT_NETWORK_ERROR = 30003,     // network error
    SHARE_RESULT_SHAREREXTENSION = 39999,   // extension code
    
    SHORTCUT_RESULT_SUCCESS = 40000,
    SHORTCUT_RESULT_FAILED = 40001
} ActionResultCode;

static map<string, string> EMPTY_MAP;

class UnitSDK;

/**
 * 异步操作的回调监听器, 当回调触发时, unitSDK 会将之前传入的 callbackId 透传回来
 * 可以在 infoMap 中取出 infoMap["callbackId"] 字段使用, 接入层根据 id 从监听器 Map 中取出响应的回调执行
 */
class ActionResultListener
{
public:
    // code: the result code to indicate the situation of execution
    // msg : the information of callback
    // infoMap: the extra info
    virtual void onActionResult(UnitSDK* pUnitSDK, ActionResultCode code, const char* msg, map<string, string> infoMap= EMPTY_MAP) = 0;
};

class UnitSDK {
public:
    virtual ~UnitSDK(){};

    static UnitSDK* getInstance();

    virtual void init(string appKey,string appSecret,string privateKey) = 0;
    
    virtual void setAsyncActionResultListener(ActionResultListener* listener) = 0;
    virtual ActionResultListener* getAsyncActionResultListener() = 0;

    virtual bool isFunctionSupported(string functionName) = 0;
    virtual bool isLogined() = 0;

    virtual string getUserID() = 0; // if not login, return empty string
    virtual string getOrderId() = 0;
    virtual string getChannelId() = 0;

    virtual void login(string callbackId) = 0;
    virtual void payForProduct(string callbackId, string info) = 0;
    virtual void share(string callbackId, string info) = 0;
    virtual void createShortCut(string callbackId) = 0;	

    virtual string callSyncStringFunc(string funcName, string jsonParams) = 0;
    virtual void callAsyncFunc(string callbackId, string funcName, string jsonParams) = 0;

    static void resetPayState() {_paying = false; }
    static bool _paying;
    
private:
    static UnitSDK* pUnitSDK;
};

}} // namespace gplay::framework::

#endif //GPLAY_GPLAYUNITSDK_H
