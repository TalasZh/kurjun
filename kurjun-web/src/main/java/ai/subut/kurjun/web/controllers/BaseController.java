package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.web.filter.AuthorizedFilter;
import ai.subut.kurjun.web.filter.ProtocolFilter;
import ai.subut.kurjun.web.filter.ResponseFilter;
import ai.subut.kurjun.web.filter.SecurityFilter;
import ninja.FilterWith;


@FilterWith( { ProtocolFilter.class, SecurityFilter.class, AuthorizedFilter.class, ResponseFilter.class })
public class BaseController
{

}
