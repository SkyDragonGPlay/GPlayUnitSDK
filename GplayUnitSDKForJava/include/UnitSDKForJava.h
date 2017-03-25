#ifndef __UNITSDKFORJAVA_H__
#define __UNITSDKFORJAVA_H__

#include "UnitSDK.h"
#include "string.h"

using namespace std;
namespace gplay { namespace framework {

class AsyncActionResultListener : public ActionResultListener {
public:
    virtual void onActionResult(UnitSDK* pUnitSDK, ActionResultCode code, const char* msg, map<string, string> infoMap= EMPTY_MAP);
};

class UnitSDKForJava
{
public:
    static UnitSDKForJava * getInstance();

    void init( string appkey, string appSecret, string privateKey );

    bool isFunctionSupported( string functionName );
    void setDebugMode(bool bDebug);
    bool isLogined();
    void resetPayState();

    void login(string callbackId);
    void payForProduct(string callbackId, map<string, string> info);
    void share(string callbackId, map<string, string> info);
    void createShortcut(string callbackId, map<string, string> info);

    string getChannelId();
    string getUserId();
    string getOrderId();

    void callSyncFunc(char const* functionName, vector<TypeParam*> params);
    string callSyncStringFunc(const char* funcName, vector<TypeParam*> params) ;
    int callSyncIntFunc(const char* funcName, vector<TypeParam*> params);
    bool callSyncBoolFunc(const char* funcName, vector<TypeParam*> params);
    float callSyncFloatFunc(const char* funcName, vector<TypeParam*> params);

    void callAsyncFunc(char const* callbackId, char const* functionName, vector<TypeParam*> params);

private:
    UnitSDKForJava();
    virtual ~UnitSDKForJava();
    bool isDebug;

    static AsyncActionResultListener* _pAsyncActionResultListener;

    static UnitSDKForJava* _pUnitSdkForJavaInstance;
    UnitSDK* _pUnitSdkInstance;

};

}} // namespace gplay::framework::

#endif
