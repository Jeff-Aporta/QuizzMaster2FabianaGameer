function CrearUsuario({ setId }) {
  const [type, setType] = React.useState("password");
  return (
    <Paper className="pad-10px max-w-700px">
      <Tooltip title="Inicio" placement="left">
        <IconButton
          size="small"
          onClick={() => setId("index")}
          style={{ opacity: 0.5 }}
        >
          <i class="fa-solid fa-chevron-left"></i>
        </IconButton>
      </Tooltip>
      <div className="pad-20px">
        <Typography variant="h4" className="tt-uppercase">
          Crear cuenta
        </Typography>
        <br />
        <hr />
        <br />
        <TextField
          id="nombre-de-usuario"
          label="Nombre de usuario"
          variant="outlined"
          fullWidth
        />
        <br />
        <br />
        <div className="d-flex">
          <TextField
            id="pass-de-usuario"
            label="Contraseña"
            variant="outlined"
            type={type}
            fullWidth
          />
          <IconButton
            onClick={() => setType(type === "password" ? "text" : "password")}
          >
            {(() => {
              if (type === "password") {
                return <i className="fa-solid fa-eye"></i>;
              } else {
                return <i className="fa-solid fa-eye-slash"></i>;
              }
            })()}
          </IconButton>
        </div>
      </div>
    </Paper>
  );
}
