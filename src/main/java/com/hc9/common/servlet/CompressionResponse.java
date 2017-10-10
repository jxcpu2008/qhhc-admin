package com.hc9.common.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**   
 * Filename:    CompressionResponse.java   
 * @version:    1.0   
 * @since:  JDK 1.7.0_25  
 * Create at:   2014年4月9日 下午1:59:53   
 * Description:  
 
 */

public class CompressionResponse extends HttpServletResponseWrapper {

    protected HttpServletResponse response;
    private ServletOutputStream out;
    private CompressedStream compressedOut;
    private PrintWriter writer;
    protected int contentLength;

    /**
     * 创建一个新的被压缩响应给HTTP
     * 
     * @param response
     *            the HTTP response to wrap.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public CompressionResponse(HttpServletResponse response) throws IOException {
        super(response);
        this.response = response;
        compressedOut = new CompressedStream(response.getOutputStream());
    }

    /**
     * Ignore attempts to set the content length since the actual content length
     * will be determined by the GZIP compression.
     * 
     * @param len
     *            the content length
     */
    public void setContentLength(int len) {
        contentLength = len;
    }

    /** @see HttpServletResponse * */
    public ServletOutputStream getOutputStream() throws IOException {
        if (null == out) {
            if (null != writer) {
                throw new IllegalStateException(
                        "getWriter() has already been called on this response.");
            }
            out = compressedOut;
        }
        return out;
    }

    /** @see HttpServletResponse * */
    public PrintWriter getWriter() throws IOException {
        if (null == writer) {
            if (null != out) {
                throw new IllegalStateException(
                        "getOutputStream() has already been called on this response.");
            }
            writer = new PrintWriter(compressedOut);
        }
        return writer;
    }

    /** @see HttpServletResponse * */
    public void flushBuffer() {
        try {
            if (writer != null) {
                writer.flush();
            } else if (out != null) {
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** @see HttpServletResponse * */
    public void reset() {
        super.reset();
        try {
            compressedOut.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** @see HttpServletResponse * */
    public void resetBuffer() {
        super.resetBuffer();
        try {
            compressedOut.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * Title: close
     * </p>
     * <p>
     * Description: 完成压缩数据写入输出流
     * </p>
     * 
     * @thrs IOException
     *             IOException
     */
    public void close() throws IOException {
        compressedOut.close();
    }
}
