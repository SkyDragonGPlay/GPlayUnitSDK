/** @file TypeParam.h
 */
#ifndef __GPLAY_TYPE_PARAM_H__
#define __GPLAY_TYPE_PARAM_H__

#include <map>
#include <string>
using namespace std;

namespace gplay { namespace framework {

/**   
 *  @class  TypeParam  
 */
class TypeParam
{
public:
    /**
     @brief the default constructor of TypeParam
     */
    TypeParam();

    virtual ~TypeParam();
    /**
     @brief the constructor of TypeParam
     @param the value is Integer
     */
    TypeParam(int nValue);
    /**
     @brief the  constructor of TypeParam
     @param the value is float
     */
    TypeParam(float fValue);
    /**
     @brief the constructor of TypeParam
     @param the value is boolean
     */
    TypeParam(bool bValue);
    /**
     @brief the default constructor of TypeParam
     @param the value is char
     */
    TypeParam(const char* strValue);
    /**
     @brief the default constructor of TypeParam
     @param the value is StringMap
     */
    TypeParam(std::map<std::string, std::string> strMapValue);

	typedef enum{
		kParamTypeNull = 0,
		kParamTypeInt,
		kParamTypeFloat,
		kParamTypeBool,
		kParamTypeString,
        kParamTypeStringMap,
		kParamTypeMap,
	} ParamType;
    /**
     @brief get the ParamType of value
     */
	inline ParamType getCurrentType() {
		return _type;
	}
    /**
     @brief get the int value
     */
	inline int getIntValue() {
		return _intValue;
	}
    /**
     @brief get the float value
     */
	inline float getFloatValue() {
		return _floatValue;
	}
    /**
     @brief get the boolean value
     */
	inline bool getBoolValue() {
		return _boolValue;
	}
    /**
     @brief get the char value
     */
	inline const char* getStringValue() {
		return _strValue.c_str();
	}
    /**
     @brief get the map of  value
     */
	inline std::map<std::string, TypeParam *> getMapValue() {
		return _mapValue;
	}
    /**
     @brief get the StringMap value
     */
	inline std::map<std::string, std::string>  getStrMapValue() {
        return _strMapValue;
    }

private:
    /**
     @brief the  constructor of TypeParam
     @param the  map of value
     */
    TypeParam(std::map<std::string, TypeParam *> mapValue);

private:
	ParamType _type;

	int _intValue;
	float _floatValue;
	bool _boolValue;
	std::string _strValue;
	std::map<std::string, TypeParam *> _mapValue;
    std::map<std::string, std::string> _strMapValue;
};

}} // namespace gplay::framework::

#endif /* __GPLAY_TYPE_PARAM_H__ */
