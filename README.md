# cordova-plugin-wswAlipay
ionic angular 支付宝插件 包含支付宝登录和支付宝支付

添加方法
```
cordova plugin add https://github.com/MaXiaosuai/cordova-plugin-wswAlipay --variable alipayid=随便什么标志
npm i wsw-alipay
```

ts中
```
import { wswAlipay } from 'wsw-alipay';
```
```
///登录
wswAlipay.aliLogin({'sign':'后台传过来的'}).then((res)=>{
  alert('登录成功'+res);
}).catch((err)=>{
  alert(err);
})
```
```
///支付
 wswAlipay.aliPay({'sign':'后台传过来的'}).then((res)=>{
  alert('支付成功');
}).catch((err)=>{
  alert(err);
})
```

# 好用给点个星星啦 哈哈哈
