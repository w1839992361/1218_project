// stores/video.js
import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useVideosInfo = defineStore('videos', () => {
    const videos = ref({});

    function initVideoData(id) {
        if (!videos.value[id]) {
            videos.value[id] = {
                rating: 0,
                likes: 0,
                favorites: false,
                views: 0,
            };
        }
    }

    function setRating(id, value) {
        initVideoData(id);
        videos.value[id].rating = value;
    }

    function likeVideo(id) {
        initVideoData(id);
        videos.value[id].likes += 1;
    }

    function toggleFavorite(id) {
        initVideoData(id);
        videos.value[id].favorites = !videos.value[id].favorites;
    }

    function incrementViews(id) {
        initVideoData(id);
        videos.value[id].views += 1;
    }

    function getVideoData(id) {
        initVideoData(id);
        return videos.value[id];
    }

    return {
        videos,
        setRating,
        likeVideo,
        toggleFavorite,
        incrementViews,
        getVideoData,
    };
}, {
    persist: true,
});
