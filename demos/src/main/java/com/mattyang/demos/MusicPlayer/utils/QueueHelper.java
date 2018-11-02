package com.mattyang.demos.MusicPlayer.utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import com.mattyang.demos.MusicPlayer.VoiceSearchParams;
import com.mattyang.demos.MusicPlayer.model.MusicProvider;

import java.util.ArrayList;
import java.util.List;

public class QueueHelper {
    private static final String TAG = LogHelper.makeLogTag(QueueHelper.class);
    private static final int RANDOM_QUEUE_SIZE = 10;

    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(String mediaId,
                                                                     MusicProvider musicProvider){
        String[] hierarchy = MediaIDHelper.getHierarchy(mediaId);
        if(hierarchy.length != 2){
            LogHelper.e(TAG,"Cloud not build a playing queue for this mediaId: ",mediaId);
            return null;
        }
        String categoryType = hierarchy[0];
        String categoryValue = hierarchy[1];
        LogHelper.d(TAG,"Creating playing queue for ",categoryType,", ",categoryValue);
        Iterable<MediaMetadataCompat> tracks = null;
        if(categoryType.equals(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE)){
            tracks = musicProvider.getMusicsByGenre(categoryValue);
        }else if(categoryType.equals(MediaIDHelper.MEDIA_ID_MUSICS_BY_SEARCH)){
            tracks = musicProvider.searchMusicBySongTitle(categoryValue);
        }

        if(tracks == null){
            LogHelper.e(TAG,"Unrecognized category type: ",categoryType," for media ",mediaId);
            return null;
        }
        return convertToQueue(tracks,hierarchy[0],hierarchy[1]);
    }

    public static List<MediaSessionCompat.QueueItem> getPlayingQueueFromSearch(String query,
                                                                               Bundle queryParms,
                                                                               MusicProvider musicProvider){
        LogHelper.d(TAG,"Creating playing queue for musics from search: ",query," params=",queryParms);
        VoiceSearchParams params = new VoiceSearchParams(query,queryParms);
        LogHelper.d(TAG,"VoiceSearchParams: ",params);
        if(params.isAny){
            return getRandomQueue(musicProvider);
        }
        List<MediaMetadataCompat> result = null;
        if(params.isAlbumFocus){
            result = musicProvider.searchMusicByAlbum(params.album);
        }else if(params.isGenreFocus){
            result = musicProvider.getMusicsByGenre(params.genre);
        }else if(params.isArtistFocus){
            result = musicProvider.searchMusicByArtist(params.artist);
        }else if(params.isSongFocus){
            result = musicProvider.searchMusicBySongTitle(params.song);
        }
        if(params.isUnstructured || result == null || !result.iterator().hasNext()){
            result = musicProvider.searchMusicBySongTitle(query);
            if(result.isEmpty()){
                result = musicProvider.searchMusicByGenre(query);
            }
        }
        return convertToQueue(result,MediaIDHelper.MEDIA_ID_MUSICS_BY_SEARCH,query);
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,String mediaId){
        int index = 0;
        for(MediaSessionCompat.QueueItem item : queue){
            if(mediaId.equals(item.getDescription().getMediaId())){
                return index;
            }
            index++;
        }
        return -1;
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue, long queueId){
        int index = 0;
        for(MediaSessionCompat.QueueItem item : queue){
            if(queueId == item.getQueueId()){
                return index;
            }
            index++;
        }
        return -1;
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(Iterable<MediaMetadataCompat> tracks,
                                                                     String... categories){
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        int count = 0;
        for(MediaMetadataCompat track : tracks){
            String hierarchyAwareMediaID = MediaIDHelper.createMediaID(track.getDescription().getMediaId(),categories);
            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track).putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,hierarchyAwareMediaID)
                    .build();
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(trackCopy.getDescription(),count++);
            queue.add(item);
        }
        return queue;
    }

    public static List<MediaSessionCompat.QueueItem> getRandomQueue(MusicProvider musicProvider){
        List<MediaMetadataCompat> result = new ArrayList<>(RANDOM_QUEUE_SIZE);
        Iterable<MediaMetadataCompat> shuffled = musicProvider.getShuffledMusic();
        for(MediaMetadataCompat metadata : shuffled){
            if(result.size() == RANDOM_QUEUE_SIZE){
                break;
            }
            result.add(metadata);
        }
        LogHelper.d(TAG,"getRandomQueue: result.size=",result.size());
        return convertToQueue(result,MediaIDHelper.MEDIA_ID_MUSICS_BY_SEARCH,"random");
    }

    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue){
        return (queue != null && index >= 0 && index < queue.size());
    }

    public static boolean equals(List<MediaSessionCompat.QueueItem> list1,List<MediaSessionCompat.QueueItem> list2){
        if(list1 == list2){
            return true;
        }
        if(list1 == null || list2 == null){
            return false;
        }
        if(list1.size() != list2.size()){
            return false;
        }
        for(int i=0; i<list1.size();i++){
            if(list1.get(i).getQueueId() != list2.get(i).getQueueId()){
                return false;
            }
            if(!TextUtils.equals(list1.get(i).getDescription().getMediaId(),list2.get(i).getDescription().getMediaId())){
                return false;
            }
        }
        return true;
    }

    public static boolean isQueueItemPlaying(Activity context,MediaSessionCompat.QueueItem queueItem){
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(context);
        if(controller != null && controller.getPlaybackState() != null){
            long currentPlayingQueueId = controller.getPlaybackState().getActiveQueueItemId();
            String currentPlayingMediaId = controller.getMetadata().getDescription().getMediaId();
            String itemMusicId = MediaIDHelper.extractMusicIDFromMediaID(queueItem.getDescription().getMediaId());
            if(queueItem.getQueueId() == currentPlayingQueueId && currentPlayingMediaId != null &&
                    TextUtils.equals(currentPlayingMediaId,itemMusicId)){
                return true;
            }
        }
        return false;
    }
}
