package com.mojang.authlib;

import java.util.StringJoiner;

public interface Environment {
    public String getAuthHost();

    public String getAccountsHost();

    public String getSessionHost();

    public String getServicesHost();

    public String getName();

    public String asString();

    public static Environment create(final String auth, final String account, final String session, final String services, final String name) {
        return new Environment(){

            @Override
            public String getAuthHost() {
                return auth;
            }

            @Override
            public String getAccountsHost() {
                return account;
            }

            @Override
            public String getSessionHost() {
                return session;
            }

            @Override
            public String getServicesHost() {
                return services;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String asString() {
                return new StringJoiner(", ", "", "").add("authHost='" + this.getAuthHost() + "'").add("accountsHost='" + this.getAccountsHost() + "'").add("sessionHost='" + this.getSessionHost() + "'").add("servicesHost='" + this.getServicesHost() + "'").add("name='" + this.getName() + "'").toString();
            }
        };
    }
}
