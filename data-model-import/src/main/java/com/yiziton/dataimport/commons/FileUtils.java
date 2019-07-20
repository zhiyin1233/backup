package com.yiziton.dataimport.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Description:
 * @Author: xiaoHong
 * @Date: 2018/12/13
 * @Copyright © 2018 www.1ziton.com Inc. All Rights Reserved.
 */
@Slf4j
@Component
public class FileUtils {

    @Value("${saveFile.path:}")
    String path;

    public void saveFile(String data, String folder) throws IOException{
        FileOutputStream os = null;
        try{
            File file = null;
            if(StringUtils.isEmpty(path)){
                file = new File(folder+".txt");
            }else{
                File filePath = new File(path);
                if(!filePath.exists()){
                    filePath.mkdirs();
                }
                file = new File(filePath,folder+".txt");
            }

            if(!file.exists()){
                file.createNewFile();
            }
            os = new FileOutputStream(file,true);
            os.write(data.getBytes());
            os.write("\r\n".getBytes());
        }catch (IOException e){
            log.error("saveFile Exception",e);
            throw e;
        }finally {
            try{
                os.close();
            }catch (IOException e){
                log.error("saveFile Exception",e);
                throw e;
            }
        }
    }
}
