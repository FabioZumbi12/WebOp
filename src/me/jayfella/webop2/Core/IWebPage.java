package me.jayfella.webop2.Core;

import com.sun.net.httpserver.HttpExchange;

public interface IWebPage
{
    public String contentType();
    public int responseCode();
    public byte[] get(HttpExchange he);
    public byte[] post(HttpExchange he);
}
