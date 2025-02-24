import { build_SASS } from "merger-client-static-jsx";

await build_SASS({
    mainSASS: "./src/quizzmasterfx/front/scss/abrevs.scss",
    outCSS: "./src/quizzmasterfx/front/index.css",
});