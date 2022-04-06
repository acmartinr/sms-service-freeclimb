import React, {Component} from 'react';

import './Phones.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import axios from 'axios';

import Button from '@material-ui/core/Button';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';

class RemovedPhones extends Component {
  constructor(props) {
    super(props);

    this.state = {
      phones: [],
      count: 0,
      rowsPerPage: 10,
      page: 0
    };

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.downloadPhones = this.downloadPhones.bind(this);
  }

  componentDidMount() {
    this.updatePhones();
  }


  downloadPhones() {
    axios({
      url: '/api/phones/removed',
      method: 'GET',
      responseType: 'blob'
    }).then((response) => {
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'removed caller ids.csv');
      document.body.appendChild(link);
      link.click();
    });
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updatePhones);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updatePhones);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updatePhones() {
    const request = {
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getRemovedPhones(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          phones: response.data.phones,
          count: response.data.count
        });
      }
    });
  }

  render() {
    return (
      <div className='padding-top-10 max-width-600'>
        <Button
          type="button"
          variant="contained"
          color="primary"
          onClick={(e) => this.downloadPhones()}
          className="right">Export</Button>

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Phone</TableCell>
              <TableCell>User</TableCell>
              <TableCell>Date</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.phones.map(row => (
              <TableRow key={row.id}>
                <TableCell>+{row.phone}</TableCell>
                <TableCell>{row.note}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />
      </div>
    )
  }
}

export default RemovedPhones;
