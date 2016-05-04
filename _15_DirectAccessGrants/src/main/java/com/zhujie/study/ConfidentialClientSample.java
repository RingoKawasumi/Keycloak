package com.zhujie.study;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.HttpClientBuilder;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.util.BasicAuthHelper;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujie on 16/4/29.
 */
public class ConfidentialClientSample {

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClientBuilder().disableTrustManager().build();
        try {
            HttpPost post = new HttpPost(KeycloakUriBuilder.fromUri("http://localhost:8080/auth").path(ServiceUrlConstants.TOKEN_PATH).build("master"));
            List<NameValuePair> formparams = new ArrayList<>();
            formparams.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, "password"));
            formparams.add(new BasicNameValuePair("username", "admin"));
            formparams.add(new BasicNameValuePair("password", "admin"));

            boolean isPublic = false;// if client is public access type
            if (isPublic) {
                formparams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, "testa"));
            } else {
                String authorization = BasicAuthHelper.createHeader("testa", "cb9d7fba-2858-4e49-9880-3c6c315f649b");
                post.setHeader("Authorization", authorization);
            }
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(formparams, "UTF-8");
            post.setEntity(form);

            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (status != 200) {
                throw new IOException("Bad status: " + status);
            }
            if (entity == null) {
                throw new IOException("No Entity");
            }
            InputStream is = entity.getContent();
            try {
                AccessTokenResponse tokenResponse = JsonSerialization.readValue(is, AccessTokenResponse.class);
                System.out.println(tokenResponse);
//                logout(tokenResponse);
            } finally {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
        } finally {
            client.getConnectionManager().shutdown();
        }

    }


    private static void logout(AccessTokenResponse tokenResponse) throws IOException {
        HttpClient client = new HttpClientBuilder().disableTrustManager().build();
        try {
            HttpPost post = new HttpPost(KeycloakUriBuilder.fromUri("http://localhost:8080/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH).build("master"));
            List<NameValuePair> formparams = new ArrayList<>();
            boolean isPublic = false;// if client is public access type
            if (isPublic) {
                formparams.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, "testa"));
            } else {
                String authorization = BasicAuthHelper.createHeader("testa", "cb9d7fba-2858-4e49-9880-3c6c315f649b");
                post.setHeader("Authorization", authorization);
            }
            formparams.add(new BasicNameValuePair(OAuth2Constants.REFRESH_TOKEN, tokenResponse.getRefreshToken()));
            UrlEncodedFormEntity form = new UrlEncodedFormEntity(formparams, "UTF-8");
            post.setEntity(form);
            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (status != 204) {
                System.err.println(status + " " + entity);
            }
            if (entity == null) {
                return;
            }
            InputStream is = entity.getContent();
            try {
                System.out.println(IOUtils.toString(is));
            } finally {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}
