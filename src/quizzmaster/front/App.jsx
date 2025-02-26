setInterval(() => {
  if (document.querySelector(".rotate360") != null) {
    document.querySelector("style.rooted").innerHTML = `
      :root {
        --r360: ${Math.abs(Math.random()) * 0.1}deg;
      }
    `;
  }
}, 2000);

function App() {
  const [id, setId] = React.useState("crear-usuario");

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <div
        className="p-fixed expandir"
        style={{
          background: `radial-gradient(
            circle at center, 
            dodgerblue, 
            rgba(30, 144, 255, 0.25), 
            transparent 60%, 
            black
          )`,
          filter: (() => {
            switch (id) {
              case "crear-usuario":
                return `hue-rotate(60deg)`;
              case "index":
              default:
                return `hue-rotate(0deg)`;
            }
          })(),
          transition: "all 1s",
          zIndex: -2,
        }}
      ></div>
      <div
        className="p-fixed expandir"
        style={{
          background:
            "radial-gradient(circle at center, transparent 60%, black)",
          zIndex: 1,
          pointerEvents: "none",
        }}
      ></div>
      <div className="d-center min-h-100vh">
        {(() => {
          switch (id) {
            case "crear-usuario":
              return <CrearUsuario setId={setId} />;
            case "index":
            default:
              return <Index setId={setId} />;
          }
        })()}
      </div>
    </ThemeProvider>
  );
}

function Gamification_lights_radial({ width, height, style }) {
  const length = 16;
  return (
    <div
      className="p-fixed rotate360"
      style={{
        left: "50%",
        top: "50%",
        transform: "translate(-50%, -50%) rotate(var(--r360)) scale(1.5)",
        width: "1000px",
        height: "1000px",
        overflow: "hidden",
        ...style,
      }}
    >
      {Array.from({ length }, (_, i) => {
        return (
          <img
            src="img/rayo-radial.png"
            className="p-absolute"
            width={width * 0.5}
            height={i % 2 == 0 ? 200 : 40}
            style={{
              left: "50%",
              top: "50%",
              transform: `translateY(-50%) rotate(${(i * 360) / length}deg)`,
              transformOrigin: "0 50%",
            }}
          />
        );
      })}
    </div>
  );
}

function on(asunto, contenido) {
  if (!asunto || !contenido) {
    return;
  }
  if (typeof contenido == "object") {
    contenido = JSON.stringify(contenido);
  }
  switch (asunto) {
    case "test":
      //document.querySelector(".pregunta").innerHTML = contenido;
      return;
  }
}
function emit(asunto, contenido = "") {
  if (asunto == null || contenido == null || !window["Socket"]) {
    return;
  }
  if (typeof contenido == "object") {
    contenido = JSON.stringify(contenido);
  }
  Socket.emit(asunto, contenido);
}

setTimeout(() => {
  ReactDOM.render(<App />, document.getElementById("root"));
  emit("init");
});
