package pub.edholm.tacobot.Listeners

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pub.edholm.tacobot.movieapi.Imdb
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class MovieListenerTest {
    val input = """{ "title_substring": [{ "id":"tt2379308", "title":"Psycho-Pass", "name":"","title_description":"2012 TV series, Kiyotaka Suzuki","episode_title":"","description":"2012 TV series, Kiyotaka Suzuki"},{ "id":"tt4219130", "title":"Psycho-Pass: The Movie", "name":"","title_description":"2015, Katsuyuki Motohiro...","episode_title":"","description":"2015, Katsuyuki Motohiro..."},{ "id":"tt6394194", "title":"Psycho-Pass: Mandatory Happiness", "name":"","title_description":"2015 video game","episode_title":"","description":"2015 video game"}],"title_approx": [{ "id":"tt1112739", "title":"Psycho Hillbilly Cabin Massacre!", "name":"","title_description":"2007 short, Robert F. Cosnahan III","episode_title":"","description":"2007 short, Robert F. Cosnahan III"},{ "id":"tt0397545", "title":"Murderers, Mobsters & Madmen Vol. 3: Psychos and Mass Murderers", "name":"","title_description":"1992 video documentary, Nick Bougas","episode_title":"","description":"1992 video documentary, Nick Bougas"},{ "id":"tt2078709", "title":"Psycho Bettys from Planet Pussycat", "name":"","title_description":"2011, Aaron Hendren","episode_title":"","description":"2011, Aaron Hendren"},{ "id":"tt0336386", "title":"Es bedarf keiner Psychoanalyse, um festzustellen, dass das Leben ein einziger Friedhof gescheiterter Pläne ist", "name":"","title_description":"1976 TV documentary, Jörg A. Eggers","episode_title":"","description":"1976 TV documentary, Jörg A. Eggers"},{ "id":"tt0316162", "title":"Nacho: Latin Psycho II - Crazy ASSylum", "name":"","title_description":"2002 video, Nacho Vidal","episode_title":"","description":"2002 video, Nacho Vidal"},{ "id":"tt0291434", "title":"Psycho Pussy", "name":"","title_description":"2000 video, ","episode_title":"","description":"2000 video, "},{ "id":"tt4052400", "title":"Grandpa's Psycho", "name":"","title_description":"2015, Danny LeGare","episode_title":"","description":"2015, Danny LeGare"},{ "id":"tt3756608", "title":"Baadasssss Psycho Priest", "name":"","title_description":"2014 short, Christopher Dockens","episode_title":"","description":"2014 short, Christopher Dockens"},{ "id":"tt2175683", "title":"Guilt by Association: Psychoanalyzing Spellbound", "name":"","title_description":"2008 documentary short, John Cork","episode_title":"","description":"2008 documentary short, John Cork"},{ "id":"tt1931533", "title":"Seven Psychopaths", "name":"","title_description":"2012, Martin McDonagh","episode_title":"","description":"2012, Martin McDonagh"},{ "id":"tt1684915", "title":"My Super Psycho Sweet 16: Part 2", "name":"","title_description":"2010 TV movie, Jacob Gentry","episode_title":"","description":"2010 TV movie, Jacob Gentry"},{ "id":"tt2056659", "title":"My Super Psycho Sweet 16: Part 3", "name":"","title_description":"2012 TV movie, Jacob Gentry","episode_title":"","description":"2012 TV movie, Jacob Gentry"},{ "id":"tt0293532", "title":"'Psycho' Path", "name":"","title_description":"1999 video documentary short, D-J","episode_title":"","description":"1999 video documentary short, D-J"},{ "id":"tt1610327", "title":"Der Psycho Pate", "name":"","title_description":"2010 TV movie, Oliver Mielke","episode_title":"","description":"2010 TV movie, Oliver Mielke"},{ "id":"tt1606260", "title":"Kiss Psycho Circus", "name":"","title_description":"1998 video short, James Hurlburt","episode_title":"","description":"1998 video short, James Hurlburt"},{ "id":"tt4190396", "title":"Last Summer 2. Ural Psycho", "name":"","title_description":"2009 short, Viktor Murzikov","episode_title":"","description":"2009 short, Viktor Murzikov"},{ "id":"tt1686906", "title":"Psycho-Path: Mania", "name":"","title_description":"2011, Geraldine Winters","episode_title":"","description":"2011, Geraldine Winters"}] } """
    @Test
    fun extractManyImdbIds() {
        val movieListener = MovieListener()
        val extracted = movieListener.extractImdbId(input)
        assertThat(extracted.size).isEqualTo(20)
        println(extracted)

        println(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d MMMM")))
        println(LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY")))
    }

    @Test
    internal fun messageThatDoesntContainAnyIds() {
        val movieListener = MovieListener()
        val extractedIds = movieListener.extractImdbId("")
        assertThat(extractedIds.size).isEqualTo(0)
    }

    @Test
    internal fun testExtractionFromImdbLink() {
        val movieListener = MovieListener()
        val extracted = movieListener.extractImdbId("http://akas.imdb.com/title/tt0111161/?ref_=nv_sr_1")
        assertThat(extracted.size).isEqualTo(1)
        assertThat(extracted[0]).isEqualTo(Imdb.Id("tt0111161"))
    }

    @Test
    internal fun testIdInAllCaps() {
        val movieListener = MovieListener()
        val extracted = movieListener.extractImdbId("TT0111161")
        assertThat(extracted.size).isEqualTo(1)
        assertThat(extracted[0]).isEqualTo(Imdb.Id("tt0111161"))

    }
}