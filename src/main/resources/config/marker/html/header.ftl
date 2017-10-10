<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<#if session_user ? exists>
true
<#else>
false
</#if>
<#list topics as item>
	
	
	<#if item.id == activetopic!>
		<li class="active sub_nav${item.id}">
			<a href="/${item.url!}" >${item.name}</a>
		</li>
	</#if>
	
	<#if item.id != activetopic!>
		<li class="sub_nav${item.id}">
			<a href="/${item.url!}" >${item.name}</a>
		</li>
	</#if>
	
</#list>