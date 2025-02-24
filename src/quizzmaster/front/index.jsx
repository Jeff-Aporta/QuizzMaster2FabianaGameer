setInterval(() => {
  document.querySelector("style.rooted").innerHTML = `
    :root {
      --r360: ${Math.abs(Math.random()) * 0.1}deg;
    }
  `;
}, 2000);

function App() {
  const [id, setId] = React.useState("index");

  const width = 1000;
  const height = 1000;

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <div
        className="p-fixed expandir"
        style={{
          background:
            "radial-gradient(circle at center, dodgerblue, rgba(30, 144, 255, 0.25), transparent 60%, black)",
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
        <Index width={width} height={height} />
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

function Index({ width, height }) {
  const bsx = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    fontWeight: "bolder",
    fontSize: "120%",
  };
  return (
    <div className="pad-20px">
      <Gamification_lights_radial
        width={width}
        height={height}
        style={{ zIndex: -1, opacity: 0.2 }}
      />
      <center>
        <Paper className="pad-40px max-w-700px overflow-hidden" elevation={3}>
          <Typography variant="h2">QuizzMaster</Typography>
          <br />
          <hr />
          <br />
          <Button
            size="large"
            variant="contained"
            className="tt-uppercase"
            fullWidth
            endIcon={<i className="fa-solid fa-user-tie" />}
            sx={bsx}
          >
            Iniciar sesión
          </Button>
          <br />
          <br />
          <Button
            size="large"
            variant="contained"
            className="tt-uppercase"
            endIcon={<i className="fa-solid fa-user-plus" />}
            sx={bsx}
            fullWidth
          >
            Crear cuenta
          </Button>
          <br />
          <br />
          <hr />
          <br />
          <Button
            size="large"
            variant="contained"
            className="tt-uppercase"
            endIcon={<i className="fa-regular fa-rectangle-list" />}
            sx={bsx}
            fullWidth
          >
            Hacer un Quizz
          </Button>
          <br />
          <br />
          <p align="right">
            <Typography variant="caption" color="textSecondary">
              Desarrollado por <b>Fabiana</b>
              <br />
              Usando{" "}
              <big>
                <b>JavaFX</b>
              </big>
            </Typography>
          </p>
        </Paper>
      </center>
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
