# Дипломная работа курса SkillBox "Java-разработчик" 
## В рамках дипломного проекта реализован поисковый движок searchengine-master
---------------------
1. Программа создает БД, если ее не существует
2. Берет список сайтов из файла свойств проекта
3. Загружает страницы сайтов *
4. Строит индекс частоты использования лемм
5. Реализует поиск по набору лемм

---------------------
<table>
<thead>
<tr>
<th align="center">Компонент</th>
<th align="center">Ограничение</th>
</tr>
</thead>
<tbody>
<tr>
<td align="right">Версия JDK</td>
<td align="center">19</td>
</tr>
<tr>
<td align="right">Система контроля версий</td>
<td align="center">Git</td>
</tr>
<tr>
<td align="right">Версия Spring Boot</td>
<td align="center">2.7.1</td>
</tr>
<tr>
<td align="right">База данных</td>
<td align="center">MySql 8.0.28</td>
</tr>
<tr>
<td align="right">Лемматизатор</td>
<td align="center">org.apache.lucene</td>
</tr>
<tr>
<td align="right">Парсинг html</td>
<td align="center">jsoup 1.14.3</td>
</tr>
<tr>
<td align="right">Сборщик проекта</td>
<td align="center">Maven</td>
</tr>
</tbody>
</table>

###№ * Для удобства тестирования время загрузки сайта ограничено 1 секундой (ChildLink: final int TIME_LIMIT = 1000;)
