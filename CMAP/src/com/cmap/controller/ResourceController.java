package com.cmap.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.ResourceService;
import com.cmap.service.vo.ResourceServiceVO;

@Controller
@RequestMapping("/resource")
public class ResourceController extends BaseController {
    @Log
    private static Logger log;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(method=RequestMethod.GET, value="/download/{id}")
    public void download(Model model, @PathVariable("id") String fileId, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        /*
         * Big thanks to BalusC for this part
         * cf. his post on http://balusc.blogspot.ch/2007/04/imageservlet.html
         */

        // Check if ID is supplied to the request.
        if (fileId == null) {
            // Do your thing if the ID is not supplied to the request.
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Lookup Image by ImageId in database.
        // Do your "SELECT * FROM Image WHERE ImageID" thing.
        ResourceServiceVO rsVO = null;
        try {
            rsVO = resourceService.getResourceInfo(fileId);

        } catch (ServiceLayerException sle) {
            rsVO = null;
        }

        // Check if image is actually retrieved from database.
        if (rsVO == null) {
            // Do your thing if the image does not exist in database.
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Init servlet response.
        response.reset();
        response.setBufferSize(Constants.DEFAULT_BUFFER_SIZE);
        response.setContentType(rsVO.getContentType());
        //response.setHeader("Content-Length", String.valueOf(isVO.get));
        response.setHeader("Content-Disposition", "inline; filename=\"" + rsVO.getFileFullName() + "\"");

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open streams.
            try {
                input = new BufferedInputStream(rsVO.getInputStream(), Constants.DEFAULT_BUFFER_SIZE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            output = new BufferedOutputStream(response.getOutputStream(), Constants.DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

        } finally {
            // Gently close streams.
            close(output);
            close(input);
            behaviorLog(request);
        }
    }

    // Helper (can be refactored to public utility class)
    private static void close(Closeable resource) {
      if (resource != null) {
        try {
          resource.close();
        } catch (IOException e) {
          // Do your thing with the exception. Print it, log it or mail it.
          e.printStackTrace();
        }
      }
    }
}
