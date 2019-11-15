package com.cmap.utils.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cmap.Constants;
import com.cmap.exception.ServiceLayerException;
import com.cmap.utils.ProvisionUtils;

public class PingUtils implements ProvisionUtils {
    private static Logger log = LoggerFactory.getLogger(PingUtils.class);

    @Override
    public boolean doCheck(Map<String, String> paraMap) throws ServiceLayerException {
        boolean pingResult = false; // 結果
        try {
            // 檢查必要參數
            if (!paraMap.containsKey(Constants.PARA_IP_ADDRESS)) {
                throw new ServiceLayerException("未傳入 IP_ADDRESS");
            }

            final String PING_IP = paraMap.get(Constants.PARA_IP_ADDRESS);

            Runtime runtime = Runtime.getRuntime(); // 獲取當前程式的執行進物件
            Process process = null; // 宣告處理類物件
            String line = null; // 返回行資訊
            InputStream is = null; // 輸入流
            InputStreamReader isr = null;// 位元組流
            BufferedReader br = null;

            try {
                process = runtime.exec("ping " + PING_IP); // PING

                is = process.getInputStream();      // 例項化輸入流
                isr = new InputStreamReader(is);    // 把輸入流轉換成位元組流
                br = new BufferedReader(isr);       // 從位元組中讀取文字

                // 判斷執行結果訊息內容是否含有「TTL」，決定ping結果是否成功
                while ((line = br.readLine()) != null) {
                    if (line.contains("TTL")) {
                        pingResult = true;
                        break;
                    }
                }

            } catch (IOException e) {
                runtime.exit(1);

                log.error(e.toString(), e);
                throw new ServiceLayerException("PING異常 >>> [" + e.toString() + "]");

            } finally {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            }

        } catch (ServiceLayerException sle) {
            throw sle;

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("PING異常 >>> [" + e.toString() + "]");
        }
        return pingResult;
    }
}
