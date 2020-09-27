<#import "parts/page.ftlh" as page>

<@page.page>
    Hello!
    <form action="/main" method="post">
        <br><input type="text" placeholder="Country name" name="countryName_txt">
        <button type="submit">Search</button>
    </form>
    <#if (country2phones)??>
        <#list country2phones as country2phone>
            <br>${country2phone['countryName']} - ${country2phone['phoneCode']}
        </#list>
    </#if>

</@page.page>