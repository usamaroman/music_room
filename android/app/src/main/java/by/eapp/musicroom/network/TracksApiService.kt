package by.eapp.musicroom.network

import by.eapp.musicroom.network.model.tracks.CustomTrackDto
import by.eapp.musicroom.network.model.tracks.CustomTrackResponse
import by.eapp.musicroom.network.model.tracks.TrackDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TracksApiService {
    @GET(TrackEndpoints.GET_ALL_TRACKS)
    suspend fun getAllTracks(): Response<List<TrackDto>>

    @POST(TrackEndpoints.GET_ALL_TRACKS)
    suspend fun createNewTrack(@Body track: CustomTrackDto): Response<CustomTrackResponse>

    @GET(TrackEndpoints.GET_TRACK_BY_ID)
    suspend fun getTrackById(@Path("trackID")id: Int): Response<TrackDto>
}


object TrackEndpoints {
    const val GET_ALL_TRACKS = "/tracks"
    const val GET_TRACK_BY_ID = "/tracks/{id}"
}