var exec = require('cordova/exec');

module.exports = {
    aliLogin:function (arg0, success, error) {
        exec(success, error, 'wswAlipay', 'aliLogin', [arg0]);
    },
    aliPay:function (arg0,success, error) {
        exec(success, error, 'wswAlipay', 'aliPay', [arg0]);
    },
    jumpPay:function (arg0,success, error) {
        exec(success, error, 'wswAlipay', 'jumpPay', [arg0]);
    }
}
