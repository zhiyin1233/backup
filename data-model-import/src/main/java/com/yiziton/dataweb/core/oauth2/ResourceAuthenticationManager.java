/*
 * Copyright 2018 1ziton.com.
 **/
package com.yiziton.dataweb.core.oauth2;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 业务验证管理器
 *
 * @author xiaoHong
 * @create 2018-01-02
 **/
@Component("resourceAuthenticationManager")
public class ResourceAuthenticationManager implements AuthenticationManager {
    private static final Log logger = LogFactory.getLog(ResourceAuthenticationManager.class);

    private TokenStore tokenStore;
    @Value("${publicKeyValue}")
    private String keyValue;

    /*@Bean(name="keyValue")
    public String getKeyValue(){
        //todo keyValue设置
        String keyValue = null;
        if(StringUtils.isEmpty(keyValue)){
            keyValue = "-----BEGIN PUBLIC KEY-----\n" +
                    "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSznR1F0G/QjoXybjp9cz5m3Q5ckpblSytQ+3r875Lwxp8ovY71Kgq7ewAJgpw46XBXDUbv2nEEBXv4oespFLFmvn69Aft80FEY2/7YbFXvCFicaRZNAnZLb7H34TcYfAx/0JboUvcwzcCg0HDj4zuT8ctWlGXUcnpBc2yoHFkJQIDAQAB\n" +
                    "-----END PUBLIC KEY-----";
        }
        return keyValue;
    }*/

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try{
            if (authentication == null) {
                throw new InvalidTokenException("Invalid token (token not found)");
            }
            if(tokenStore == null) {
                tokenStore = jwtTokenStore();
            }
            String token = (String) authentication.getPrincipal();
            OAuth2Authentication auth = loadAuthentication(token);
            ThreadToken.token.set(token);
            return auth;
        }catch (Exception e){
            if(e instanceof OAuth2Exception || e instanceof ClientAuthenticationException){
                throw e;
            }
            throw new OAuth2Exception(e.getMessage(),e);
        }

    }


    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException,
            InvalidTokenException {

        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        else if (accessToken.isExpired()) {
            tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        result.getPrincipal();
        if (result == null) {
            // in case of race condition
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }
        return result;
    }

    @Bean
    public TokenStore jwtTokenStore() {
        TokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
        return tokenStore;
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(jwtUserAccessTokenConverter());
        String publicKey = keyValue;

        converter.setVerifierKey(publicKey);
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return converter;
    }

    @Bean
    /**
     * 自定义生成jwt信息，增加session id信息 ，key=osid
     */
    public DefaultUserAuthenticationConverter jwtUserAccessTokenConverter() {
        DefaultUserAuthenticationConverter converter = new DefaultUserAuthenticationConverter(){
            @Override
            public Authentication extractAuthentication(Map<String, ?> map) {
                if (map.containsKey(USERNAME)) {
                    User userinfo = new User();
                    userinfo.setUsername(map.get(USERNAME).toString());
                    userinfo.setUserId(map.get("userId").toString());
                    userinfo.setUserCode(map.get("userCode").toString());
                    Object osid = map.get("osid");
                    if(osid != null){
                        userinfo.setOsid(osid.toString());
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userinfo, "N/A", null);
                    return authentication;
                }
                return null;
            }
        };
        return converter;
    }
}