package com.backend.connectable.artist.service;

import com.backend.connectable.artist.domain.Artist;
import com.backend.connectable.artist.domain.Comment;
import com.backend.connectable.artist.domain.dto.ArtistComment;
import com.backend.connectable.artist.domain.repository.ArtistRepository;
import com.backend.connectable.artist.domain.repository.CommentRepository;
import com.backend.connectable.artist.ui.dto.ArtistCommentRequest;
import com.backend.connectable.artist.ui.dto.ArtistCommentResponse;
import com.backend.connectable.artist.ui.dto.ArtistDetailResponse;
import com.backend.connectable.event.domain.Event;
import com.backend.connectable.event.domain.repository.EventRepository;
import com.backend.connectable.event.mapper.EventMapper;
import com.backend.connectable.event.ui.dto.EventResponse;
import com.backend.connectable.exception.ConnectableException;
import com.backend.connectable.exception.ErrorType;
import com.backend.connectable.security.custom.ConnectableUserDetails;
import com.backend.connectable.user.domain.User;
import com.backend.connectable.user.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    public List<ArtistDetailResponse> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        return ArtistDetailResponse.toList(artists);
    }

    public ArtistDetailResponse getArtistDetail(Long artistId) {
        Artist artist = getArtist(artistId);
        return ArtistDetailResponse.from(artist);
    }

    public List<EventResponse> getArtistEvent(Long artistId) {
        List<Event> artistEvents = eventRepository.findAllEventsByArtistId(artistId);
        return artistEvents.stream()
                .map(EventMapper.INSTANCE::eventToResponse)
                .collect(Collectors.toList());
    }

    public void createComment(
            ConnectableUserDetails userDetails,
            Long artistId,
            ArtistCommentRequest artistCommentRequest) {
        Artist artist = getArtist(artistId);
        User user = getUser(userDetails);

        Comment comment =
                Comment.builder()
                        .user(user)
                        .artist(artist)
                        .contents(artistCommentRequest.getContents())
                        .build();

        commentRepository.save(comment);
    }

    private User getUser(ConnectableUserDetails userDetails) {
        return userRepository
                .findByKlaytnAddress(userDetails.getKlaytnAddress())
                .orElseThrow(
                        () ->
                                new ConnectableException(
                                        HttpStatus.BAD_REQUEST, ErrorType.USER_NOT_FOUND));
    }

    private Artist getArtist(Long artistId) {
        return artistRepository
                .findById(artistId)
                .orElseThrow(
                        () ->
                                new ConnectableException(
                                        HttpStatus.BAD_REQUEST, ErrorType.ARTIST_NOT_EXISTS));
    }

    public List<ArtistCommentResponse> getArtistComments(Long artistId) {
        List<ArtistComment> artistComments = commentRepository.getCommentsByArtistId(artistId);
        return ArtistCommentResponse.toList(artistComments);
    }
}
