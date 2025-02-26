function Index({ setId }) {
  const bsx = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    fontWeight: "bolder",
    fontSize: "120%",
  };

  const width = 1000;
  const height = 1000;

  return (
    <div className="pad-20px">
      <Gamification_lights_radial
        width={width}
        height={height}
        style={{ zIndex: -1, opacity: 0.2 }}
      />
      <center>
        <Paper className="pad-40px max-w-700px" elevation={3}>
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
            onClick={() => setId("crear-usuario")}
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

setTimeout(() => {
  ReactDOM.render(<App />, document.getElementById("root"));
  emit("init");
});
