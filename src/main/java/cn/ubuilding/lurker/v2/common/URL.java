package cn.ubuilding.lurker.v2.common;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Wu Jianfeng
 * @since 16/7/13 15:28
 */

public class URL implements Serializable {

    private String protocol;

    private String host;

    private int port;

    private String username;

    private String password;

    private String path;

    private final Map<String, String> parameters;


    public URL(String protocol, String host, int port) {
        this(protocol, host, port, null, null);
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this(protocol, host, port, null, null, path, parameters);
    }

    public URL(String protocol, String host, int port, String username, String password, String path, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        this.parameters = Collections.unmodifiableMap(parameters == null ? new HashMap<String, String>() : new HashMap<String, String>(parameters));
    }

    public static String encode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将URL 的toString() 格式转换为URL实例
     *
     * @param url URL的String格式
     */
    public static URL toURL(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url must not be null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            parameters = new HashMap<String, String>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf("/");
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.indexOf("@");
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(":");
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, host, port, username, password, path, parameters);
    }

    public String getAddress() {
        return port <= 0 ? host : host + ":" + port;
    }

    public URL addAndEncodeParameter(String key, String value) {
        if (value == null || value.length() == 0) {
            return this;
        }
        return addParameter(key, encode(value));
    }

    public String getAndDecodeParameter(String key) {
        return decode(getParameter(key));
    }

    public URL addParameter(String key, String value) {
        if (key == null || key.length() == 0 || value == null || value.length() == 0) {
            return this;
        }
        if (value.equals(getParameters().get(key))) {
            return this;
        }
        Map<String, String> params = new HashMap<String, String>(getParameters());
        params.put(key, value);
        return new URL(protocol, host, port, username, password, path, params);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String toServiceString() {
        return toStringBuilder().toString();
    }

    public String toString() {
        StringBuilder builder = toStringBuilder();
        if (getParameters() != null && getParameters().size() > 0) {
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0) {
                    if (first) {
                        builder.append("?");
                        first = false;
                    } else {
                        builder.append("&");
                    }
                    builder.append(entry.getKey());
                    builder.append("=");
                    builder.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
        return builder.toString();
    }

    private StringBuilder toStringBuilder() {
        StringBuilder builder = new StringBuilder();
        if (protocol != null && protocol.length() > 0) {
            builder.append(protocol).append("://");
        }
        if (username != null && username.length() > 0) {
            builder.append(username);
            if (password != null && password.length() > 0) {
                builder.append(":");
                builder.append(password);
            }
            builder.append("@");
        }

        if (host != null && host.length() > 0) {
            builder.append(host);
            if (port > 0) {
                builder.append(":");
                builder.append(port);
            }
        }
        if (path != null && path.length() > 0) {
            builder.append("/");
            builder.append(path);
        }
        return builder;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
